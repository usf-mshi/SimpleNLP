package edu.usfca.hs619.processors

import edu.usfca.hs619.serializers._
import edu.usfca.hs619.util.ProcessUtils
import org.apache.ctakes.typesystem.`type`.textsem._
import org.apache.uima.fit.component.JCasAnnotator_ImplBase
import org.apache.uima.fit.util.JCasUtil
import org.apache.uima.jcas.JCas
import org.slf4j.LoggerFactory

/**
  * This class implements the JCASAnnotator_ImplBase to make it a Analysis Engine.
  * This analysis engine would be the last one in the pipeline and would be used to process
  * the final JCAS structure for CTakes Pipeline
  */
class CTakesJCASProcessor extends JCasAnnotator_ImplBase{
  /**
    * This method takes JCAS as a parameters and processes the disease mentions, lab mentions, medication mentions,
    * procedure mention and sign and symptom mentions.
    * @param aJCas
    */
  override def process(aJCas: JCas): Unit = {
    val log = LoggerFactory.getLogger(this.getClass)
    log.info("Start Processing JCAS Annotators")

    val diseaseMentions = JCasUtil.select(aJCas, classOf[DiseaseDisorderMention])
    DiseaseMentionProcessor.process(diseaseMentions,ProcessUtils.getRecordMetaData(aJCas.getSofa.getLocalStringData))

    val labMentions = JCasUtil.select(aJCas,classOf[LabMention])
    LabMentionProcessor.process(labMentions,ProcessUtils.getRecordMetaData(aJCas.getSofa.getLocalStringData))

    val mediationMention = JCasUtil.select(aJCas,classOf[MedicationMention])
    MedicationMentionProcessor.process(mediationMention, ProcessUtils.getRecordMetaData(aJCas.getSofa.getLocalStringData))

    val procedureMention = JCasUtil.select(aJCas,classOf[ProcedureMention])
    ProcedureMentionProcessor.process(procedureMention,ProcessUtils.getRecordMetaData(aJCas.getSofa.getLocalStringData))

    val signSymptomMention = JCasUtil.select(aJCas,classOf[SignSymptomMention])
    SignSymptomMentionProcessor.process(signSymptomMention, ProcessUtils.getRecordMetaData(aJCas.getSofa.getLocalStringData))

  }
}
