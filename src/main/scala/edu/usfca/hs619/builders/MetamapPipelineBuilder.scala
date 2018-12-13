package edu.usfca.hs619.builders
import edu.usfca.hs619.processors.MetamapJCASProcessor
import org.apache.uima.analysis_engine.AnalysisEngine
import org.apache.uima.fit.factory.{AggregateBuilder, AnalysisEngineFactory}
import org.apache.uima.fit.pipeline.SimplePipeline
import org.apache.uima.jcas.JCas

/**
  * This is the builder for MetaMap processing
  */

object MetamapPipelineBuilder extends Builder {
  /**
    * This provides the UIMA Analysis Engine which is wrapper for the
    * gov.nih.nlm.nls.metamap.MetaMapApiImpl
    * @return org.apache.uima.analysis_engine.AnalysisEngine
    */
  override def getPipeline: AnalysisEngine = {
    AnalysisEngineFactory.createEngine(classOf[MetamapJCASProcessor])
  }

  /**
    * This method is used to process the Metamap Analysis Engine wrapper.
    * @param text The text that needs to be processed. This is a String
    * @param jCas This which is passed to U(MA org.apache.uima.fit.pipeline.SimplePipeline.runPipeline
    * @param engine This the Analysis engine which was build using the getPipeline method
    */
  override def processJCAS(text: String, jCas: JCas, engine: AnalysisEngine): Unit = {
    jCas.reset()
    jCas.setDocumentText(text)
    SimplePipeline.runPipeline(jCas, engine)
  }
}
