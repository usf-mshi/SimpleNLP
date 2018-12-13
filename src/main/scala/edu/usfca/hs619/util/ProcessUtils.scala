package edu.usfca.hs619.util
import java.io.FileNotFoundException
import java.util

import gov.nih.nlm.nls.metamap.{PCM, Utterance,Mapping,Ev}
import org.apache.ctakes.typesystem.`type`.refsem.UmlsConcept
import org.apache.ctakes.typesystem.`type`.textsem.{EventMention}
import org.postgresql.util.PSQLException
import scala.reflect.{ClassTag}
import collection.JavaConverters._

/**
  * This objects provides stateless helper methods to assist other methods
  */
object ProcessUtils {
  /**
    * All the exception handling code is consolidated into this method.
    *
    * @param e It takes the Throwable and does a generic error handling.
    */
    def handleException(e:Throwable):Unit = {
      e.getClass match {
        case q if classOf[FileNotFoundException].isAssignableFrom(q) => {
            println("@@@@Fatal Error Config file not found in the config directory Exiting the program ....")
            e.printStackTrace
            System.exit(1)
        }
        case q if classOf[PSQLException].isAssignableFrom(q) => {
          val pe = e.asInstanceOf[PSQLException]
          println("@@@@Fatal Error Config SQL Exception ...."+pe.getServerErrorMessage)
          e.printStackTrace
          System.exit(1)
        }
        case q if classOf[Throwable].isAssignableFrom(q) =>  {
          println("@@@@Fatal Error Occurred. Exiting the program")
          e.printStackTrace
          System.exit(1)
        }
      }
    }

  /**
    * This method appends the metadat string to the discharge summary. It is again accessed using JCAS sofa. It is tested
    * to make sure that cTake ignores this metadata string.
    * @param row_id Summary id. This tables contains the selected hadmid
    * @param subject_id Subject id of the discharge summary to be processed
    * @param hadm_id hadmid of the discharge summary to be processed.
    * @param text Discharge summary to which the metadata to be processed.
    * @return
    */
    def appendRecordMetaData(row_id:Int, subject_id:Int, hadm_id:Int, text:String):String = {
      return row_id.toString+"__"+subject_id.toString+"__"+hadm_id.toString+"___"+text
    }

  /**
    * This method is used to extract the metadata list from  discharge summary. In future this process would be improved
    * There should not be a need to append the metadata to the discharge summary
    * @param text Discharge summary with the metadata
    * @return returns the metadata list
    */
  def getRecordMetaData(text:String):String = {
      return  text.split("___",2)(0).split("__").mkString(",")
    }

  /**
    * This method removes the meta data appended to the discharge summary. This method is needed for MetaMap.
    * When the whole discharge summary is passed in it splits into multiple sentences to process. This method used to
    * separate the next from the metadata.
    * @param text
    * @return returns the discharge summary without the metadata.
    */
  def getTextWihtoutMetaData(text:String):String = {
    return text.split("___",2)(1)
  }

  /**
    * This method processes the ctakes mentions.
    * @param col Collection of the ctakes mentions
    * @param dbMetaData Collection of Metamap Utterances
    * @param tableName DB metadata string with summary id, subject id and the hadmid.
    * @tparam Mention The table the data needs to be inserted.
    */
  def processCtakesMention[Mention:ClassTag](col:util.Collection[Mention], dbMetaData:String, tableName:String):Unit = {
    println("Size of the Disease Discorder Mention is "+col.size())
    val map = scala.collection.mutable.HashMap.empty[String, Int]
    val cuiLlist = scala.collection.mutable.ListBuffer.empty[String]

    if(col != null){
      for(t:Mention <- col.asScala if t != null) {
        val m = t.asInstanceOf[EventMention]
        val garray = m.getOntologyConceptArr
        if(garray != null){
          for (mention <-  garray.asScala if mention != null) {
            val c:UmlsConcept = mention.asInstanceOf[UmlsConcept]
            val n = map.getOrElse(c.getCui, 0)
            map += (c.getCui -> (n + 1))
            println("#### Metadata Size Used "+dbMetaData.size)
            val begin = m.getBegin - dbMetaData.size - 5
            val end   = m.getEnd - dbMetaData.size - 5
            val valString = dbMetaData+" ,  '"+ c.getCui + "' , '" +  c.getCode +"', '"+c.getCodingScheme+"' , '"+c.getTui+"' , CURRENT_TIMESTAMP , " + begin + "  ,  " + end + "  "
            print(valString)
            DBUtils.insertGenericData(tableName = tableName, columnList = "dis_summary_id, subject_id, hadm_id, cui, code, coding_scheme, tui,timestamp, begin_offset, end_offset, covered_text,cui_text", insertValueList =valString, coveredText=m.getCoveredText,cuiText=c.getPreferredText)
          }
        }

        println("---");println
      }
    }

    println(map)
  }

  /**
    * This method process the metamap collection utterances
    * @param col Collection of Metamap Utterances
    * @param dbMetaData DB metadata string with summary id, subject id and the hadmid.
    * @param tableName The table the data needs to be inserted.
    */
  def processMetamapUttrance(col:util.Collection[Utterance],dbMetaData:String, tableName:String):Unit = {
    if(col != null){
      for(utterance:Utterance <- col.asScala if utterance != null){
        println("Displaying Utterance " + utterance)
        println("Number of PCMList " + utterance.getPCMList.size)
        println("Utterance Text "+utterance.getString)
        println("Utterance position "+utterance.getPosition)
        for (pcm:PCM <- utterance.getPCMList.asScala) {
            for (map:Mapping <- pcm.getMappingList.asScala if map != null) {
              for (mapEv:Ev <- map.getEvList.asScala if mapEv != null) {
                println("pcm phrase " +pcm.getPhrase.getPhraseText)
                println("pcm position " + pcm.getPhrase.getPosition)
                println(mapEv.getSources)
                println(mapEv.getSemanticTypes)
                println(mapEv.getConceptId)
                println(mapEv.getConceptName)
                println(mapEv.getNegationStatus)
                println(mapEv.getPreferredName)
                println("-----\n")
                val valString = dbMetaData+" ,  '"+ mapEv.getConceptId + "' , '" +  mapEv.getSemanticTypes +"', '"+mapEv.getSources+"' "+" , CURRENT_TIMESTAMP , " + pcm.getPhrase.getPosition.getX + "  ,  " + pcm.getPhrase.getPosition.getY + "  "
                print(valString)
                DBUtils.insertGenericData(tableName = tableName, columnList = "dis_summary_id, subject_id, hadm_id, cui, semantic, sources, timestamp, begin_offset, end_offset, covered_text,cui_text", insertValueList =valString, coveredText="",cuiText=mapEv.getConceptName)

              }
            }
          }
      }
    }
  }

}
