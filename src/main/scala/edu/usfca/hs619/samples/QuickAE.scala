package edu.usfca.hs619.samples

import org.apache.ctakes.typesystem.`type`.refsem.UmlsConcept
import org.apache.ctakes.typesystem.`type`.textsem.DiseaseDisorderMention
import org.apache.uima.cas.FeatureStructure
import org.apache.uima.fit.util.JCasUtil
import org.apache.uima.jcas.JCas
import org.apache.uima.jcas.cas.FSArray

class QuickAE extends org.apache.uima.fit.component.JCasAnnotator_ImplBase{
  override def process(aJCAS:JCas): Unit = {
    println(">>>> i'am working")
    val all = JCasUtil.selectAll(aJCAS)
    println("all.size(): " + all.size())
    val diseaseOrDisorders = JCasUtil.select(aJCAS, classOf[DiseaseDisorderMention])
    for(d <- diseaseOrDisorders.toArray){
      val xx:DiseaseDisorderMention =  d.asInstanceOf[DiseaseDisorderMention]
      val fs:FSArray = xx.getOntologyConceptArr
      val arrayStructure:Array[FeatureStructure] = fs.toArray
      for (fsi <- arrayStructure){
        val umlsc = fsi.asInstanceOf[UmlsConcept]
        print("feature structure "+fsi)
      }
    }
    println("disease or disorder "+diseaseOrDisorders)
    //    val rawText = aJCAS.getDocumentText
    //    println(">>>>>> raw text: "+rawText)
    //    val regex = "[A-Z]+".r
    //    val matches = regex.findAllMatchIn(rawText)
    //    println("matches:")
    //    for (m <-matches){
    //      println("> "+m)
    //      val annotation = new Annotation(aJCAS)
    //      annotation.setBegin(m.start)
    //      annotation.setEnd(m.end)
    //      annotation.addToIndexes()
    //      println("annotation added: " + annotation)
    //    }
  }

}
