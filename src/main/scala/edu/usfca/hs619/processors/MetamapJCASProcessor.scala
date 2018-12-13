package edu.usfca.hs619.processors

import java.util
import edu.usfca.hs619.serializers.MetaMapUtteranceProcessor
import edu.usfca.hs619.util.ProcessUtils
import gov.nih.nlm.nls.metamap.MetaMapApiImpl
import org.apache.uima.fit.component.JCasAnnotator_ImplBase
import org.apache.uima.jcas.JCas
import collection.JavaConverters._

/**
  * This class implements the JCASAnnotator_ImplBase to make it a Analysis Engine.
  * This analysis engine is a wrapper for MetaMap
  * @author Anil Kemisetti
  */
class MetamapJCASProcessor extends JCasAnnotator_ImplBase
{
  override def process(aJCas: JCas): Unit = {
    val text = aJCas.getDocumentText
    val api = new MetaMapApiImpl

    val theOptions: util.List[String] = new util.ArrayList[String]
    theOptions.add("-y") // Use WSD
    theOptions.add("-c") // show candidates
    theOptions.add("-s") // short semantic types
    theOptions.add("-I") // show CUIs
    theOptions.add("--negex") // enable negation
    theOptions.add("-R") // Restrict to Sources
    theOptions.add("SNOMEDCT_US,RXNORM,ICD10CM,LNC,NDFRT,RXNORM") // No space between arguments
    theOptions.add("-J") // Restrict to semantic types
    theOptions.add("aggp,bpoc,clna,clnd,diap,dsyn,fndg,hlca,lbpr,lbtr,medd,menp,mobd,orga,orgf,phsu,qlco,qnco,socb,sosy,tmco,topp,vita")

    api.setOptions(theOptions)

    if (text.trim.length > 0) {
      val _metadata = ProcessUtils.getRecordMetaData(text)
      println("#### Metamap "+_metadata)
      val _text = ProcessUtils.getTextWihtoutMetaData(text)
      val resultList = api.processCitationsFromString(_text)
      for(result <- resultList.asScala if resultList != null)
      if (result != null) {
          MetaMapUtteranceProcessor.process(result.getUtteranceList,_metadata)
      }

    }
  }
}
