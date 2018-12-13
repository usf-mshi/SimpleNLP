package edu.usfca.hs619.util
import java.sql.{Connection, DriverManager}
import edu.usfca.hs619.builders.{Builder, CTakesFastPipelineBuilder}
import org.apache.uima.analysis_engine.AnalysisEngine
import org.apache.uima.fit.factory.JCasFactory
import org.apache.uima.jcas.JCas
import org.json4s._
import org.json4s.native.JsonMethods._
import org.slf4j.LoggerFactory

import scala.io.Source

/**
  * This scala object is used to handle all the DB related activities. The highlevel apis are driven by the
  * persistent strategy. This objects includes all the DB processing apis and also the high level apis at the
  * same time. Eventually these high-level apis are exposed through SimpleNLP class.
  * In this class the two high level apis are
  *  (i) processDischargeSummaries for processing all the summaries.
  *  (ii) processDischargeSummaryText for processing a text
  *  (iii) getSampleDischargeSummary to get a single discharge summary
  */
object DBUtils {
    implicit val formats = DefaultFormats

  val log = LoggerFactory.getLogger(this.getClass)

  /**
    * This method reads the DB configuration details from the config file and creates the DB connection
    * @return java.sql.Connection
    */
  def createDBConnection():Connection ={
         println("Getting the connection ....")
         val json = readConfig()
         try{
           Class.forName((json \ "db" \ "driver").extract[String]) // Load the driver
         } catch {case e:Throwable => ProcessUtils.handleException(e)}

         val connection:Option[Connection] = try {
           Some(DriverManager.getConnection((json \"db" \ "url").extract[String],
             (json \ "db" \ "username").extract[String],
             (json \ "db" \  "password").extract[String]))
         } catch {
           case e:Throwable => {
             ProcessUtils.handleException(e)
             None
           }
         }


         println(connection)
         connection.get
     }

  /**
    * This method reads the configuration file and creates the JValue.
    * @return org.json4s.JValue
    */
  def readConfig():JValue = {
      val config:Option[String] = try {
        Some(Source.fromFile("config/config.json").mkString)
      } catch {
        case e:Throwable => {
          ProcessUtils.handleException(e)
          None
        }
      }
      parse(config.get)
    }


  /**
    * This method returns a sample discharge summary. It creates a new connections and closes the connection at the end.
    * @param hadmId It is the hadmid to get one discharge summary. Default hadmId is 121938
    * @return the sample discharge summary
    */
  def getSampleDischargeSummary(hadmId: => Int=121938):String = {

      val connection:Connection = createDBConnection()

      val text:Option[String] = try {

        Some(
        {var t = ""
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("select g.row_id, g.subject_id,g.hadm_id, n.text from noteevents n, m_goldstandard g where n.subject_id = g.subject_id and n.hadm_id = g.hadm_id and g.hadm_id = "+ hadmId + " and n.category = 'Discharge summary' limit 1")
        while ( resultSet.next() ) {
          val _text = resultSet.getString("text")
          val _row_id = resultSet.getInt("row_id")
          val _subject_id = resultSet.getInt("subject_id")
          val _hadm_id = resultSet.getInt("hadm_id")

          t= ProcessUtils.appendRecordMetaData(_row_id,_subject_id,_hadm_id,_text)
        }
        t})
      } catch {
        case e:Throwable => {
          ProcessUtils.handleException(e)
          None
        }

      } finally {
        connection.close()
      }
      text.get
    }

  /**
    * This method truncates all the DB tables. Currently the table names are hardcoded. In future this
    * list would be obtained from config file.
    * @param con If a connection is passed it would be used. If one is not provided it will create one and closes it.
    * @return a success message
    */
    def truncateTables(con: => Connection = null):String = {
      val _con = if(con == null) createDBConnection() else con
      val tabale_list = List("m_ct_disease_concept_1","m_ct_lab_concept_1","m_ct_medication_concept_1",
        "m_ct_procedure_concept_1","m_ct_symptom_concept_1","m_mm_concept_1")
      for(table <- tabale_list){
        println("#### Table about to Truncated is "+ table)
        val _statement = _con.prepareStatement(s"TRUNCATE TABLE $table")
        _statement.executeUpdate()
      }

      if(con == null) _con.close()
      "Success"
    }

  /**
    * This is a generic method to insert a Mention. Currently it uses a direct insert method. This will be improved to
    * use a ORM strategy in future.
    * @param con If a connection is passed it would be used. If one is not provided it will create one and closes it.
    * @param tableName Table to be used for insertion.
    * @param columnList The column list of values to be inserted.
    * @param insertValueList The value list to be inserted.
    * @param coveredText The CUI text to be inserted.
    * @param cuiText CUI text to be inserted.
    * @return a success message if the insertion is successful.
    */
    def insertGenericData(con: => Connection = null, tableName: => String, columnList: => String,
                          insertValueList: => String, coveredText: => String, cuiText:String):String = {

      val _con = if(con == null) createDBConnection() else con
      val s = s"INSERT INTO $tableName ($columnList) VALUES ($insertValueList, ?, ?) "
      print(s)
      val _statement =  _con.prepareStatement(s)
      _statement.setString(1,coveredText )
      _statement.setString(2, cuiText)
      val res = _statement.executeUpdate()
      log.info("#### result of insertion is " + res)
      if(con == null) _con.close()

      return "Success"
    }

  /**
    * This method process a set of discharge summaries and inserts the concepts into the database. Currently it works
    * with a fixed set of predetermined tables. In future there would be more flexibility with the table names.
    *
    * @param builder This is the pipeline builder. Default one is CTakesFastPipelineBuilder
    * @param con If a connection is passed it would be used. If one is not provided it will create one and closes it.
    * @param engine This is the Analysis Engine created previously. If one is not passed it will create one.
    * @param jcas This is the JCAS objeect. If one is not passed a new one will be created
    * @param cnt Number of discharge summaries to be processed. In future this would be improved to handle inclusion
    *            and exclusion.
    * @return It would a success message if everything works fine.
    */
    def processDischargeSummaries(builder: => Builder = CTakesFastPipelineBuilder, con: => Connection = null,
                                  engine: => AnalysisEngine = null, jcas: => JCas = null, cnt: =>Integer = 1):String = {
      val _con = if(con == null){
        log.info("#### Using Default DB Connection in DBUtils.processDischargeSummaries")
        createDBConnection()
      }else con

      val _engine = if(engine == null){
        log.info("#### Using Default engine in DBUitls.processDischargeSummaries")
        builder.getPipeline
      } else engine

      val _jcas = if(jcas == null){
        log.info("#### Using Default jcas in DBUitls.processDischargeSummaries")
        JCasFactory.createJCas
      } else jcas

      val _statement = _con.prepareStatement("select g.row_id, g.subject_id,g.hadm_id, n.text from noteevents n, m_goldstandard g where n.subject_id = g.subject_id and n.hadm_id = g.hadm_id and n.category = 'Discharge summary' and n.hadm_id not in (121938,133276) limit ?")
      _statement.setInt(1,cnt)
      val rs = _statement.executeQuery()

      while(rs.next()){
        val _text = rs.getString("text")
        val _row_id = rs.getInt("row_id")
        val _subject_id = rs.getInt("subject_id")
        val _hadm_id = rs.getInt("hadm_id")
        print(_text)
        val _metadata = ProcessUtils.appendRecordMetaData(_row_id,_subject_id,_hadm_id,_text)
        println("#### Metadata Size for Offset " + (_metadata.size - _text.size))
        builder.processJCAS(ProcessUtils.appendRecordMetaData(_row_id,_subject_id,_hadm_id,_text),_jcas,_engine)
      }


      if(con == null){
        log.info("#### Closing connection in DBUitls.processDischargeSummaries")
        _con.close()
      }
      "Success"
    }

  /**
    * This method a utility method which processes only a single discharge summary
    * @param builder This is the pipeline builder. Default one is CTakesFastPipelineBuilder
    * @param text If a connection is passed it would be used. If one is not provided it will create one and closes it.
    * @param engine This is the Analysis Engine created previously. If one is not passed it will create one.
    * @param jcas This is the JCAS objeect. If one is not passed a new one will be created
    */
    def processDischargeSummaryText(builder: => Builder = CTakesFastPipelineBuilder,
                                    text: => String = DBUtils.getSampleDischargeSummary(),
                                    engine: => AnalysisEngine = null, jcas: => JCas = null) = {
      val _engine = if(engine == null){
        log.info("#### Using Default engine in DBUitls.processDischargeSummaries")
        builder.getPipeline
      } else engine

      val _jcas = if(jcas == null){
        log.info("#### Using Default jcas in DBUitls.processDischargeSummaries")
        JCasFactory.createJCas
      } else jcas

       builder.processJCAS(text,_jcas,_engine)
    }

}

/**
  * Just to test the standalone object
  */
object testMain extends App {
  print(DBUtils.getSampleDischargeSummary())
}
