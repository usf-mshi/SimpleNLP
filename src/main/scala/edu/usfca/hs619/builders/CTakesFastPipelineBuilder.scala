package edu.usfca.hs619.builders

import edu.usfca.hs619.processors.CTakesJCASProcessor
import org.apache.uima.analysis_engine.AnalysisEngine
import org.apache.uima.fit.factory.{AggregateBuilder, AnalysisEngineFactory, JCasFactory}
import org.apache.uima.fit.pipeline.SimplePipeline
import org.apache.uima.jcas.JCas
import org.slf4j.LoggerFactory

/**
  * This is the builder for cTakes Fast Pipeline Builder
  */

object CTakesFastPipelineBuilder extends Builder {

  val log = LoggerFactory.getLogger(this.getClass)

  /**
    * This return the analysis engine which has the CTakesJCASProcessor at the edge.
    * This processor helps in serialization of the JCAS.
    * It uses the default descriptor
    * "desc.ctakes-clinical-pipeline.desc.analysis_engine.AggregatePlaintextFastUMLSProcessor"
    * @return org.apache.uima.analysis_engine.AnalysisEngine
    */
  override def getPipeline:AnalysisEngine ={
      log.info("##### Getting the pipeline")
      val ae = AnalysisEngineFactory.createEngineDescription("desc.ctakes-clinical-pipeline.desc.analysis_engine.AggregatePlaintextFastUMLSProcessor")
      val qae = AnalysisEngineFactory.createEngineDescription(classOf[CTakesJCASProcessor])
      val builder = new AggregateBuilder()
      builder.add(ae)
      builder.add(qae)
      val engine = builder.createAggregate();
      engine
    }

  /**
    * This method takes in a JCAS object and resets it before processing the text.
    * This is to assure that model is loaded only once.
    * @param text The text that needs to be processed. This is a String
    * @param jCas This which is passed to U(MA org.apache.uima.fit.pipeline.SimplePipeline.runPipeline
    * @param engine This the Analysis engine which was build using the getPipeline method
    */
  override def processJCAS(text:String, jCas:JCas = JCasFactory.createJCas,
                   engine:AnalysisEngine = CTakesFastPipelineBuilder.getPipeline):Unit = {
    jCas.reset()
    jCas.setDocumentText(text)
    SimplePipeline.runPipeline(jCas, engine)
  }

}
