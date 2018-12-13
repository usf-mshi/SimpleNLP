package edu.usfca.hs619.samples;
import java.util.Iterator;

import org.apache.uima.jcas.JCas;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.util.XMLInputSource;
import org.apache.uima.UIMAFramework;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.cas.FeatureStructure;

import org.metamap.uima.ts.Utterance;
import org.metamap.uima.ts.Phrase;
import org.metamap.uima.ts.Candidate;
import org.metamap.uima.ts.Mapping;
import org.metamap.uima.ts.Span;
import edu.usfca.hs619.util.DBUtils;



/**
 * Describe class UIMASampleApp here.
 *
 *
 * Created: Thu Feb  3 17:10:57 2011
 *
 * @author <a href="mailto:wrogers@nlm.nih.gov">Willie Rogers</a>
 * @version 1.0
 */
public class UIMASampleApp {

    AnalysisEngine ae;
    JCas jcas;

    /**
     * Creates a new <code>UIMASampleApp</code> instance.
     *
     */
    public UIMASampleApp()
            throws org.apache.uima.cas.CASRuntimeException,
            org.apache.uima.analysis_engine.AnalysisEngineProcessException,
            org.apache.uima.resource.ResourceInitializationException,
            org.apache.uima.util.InvalidXMLException,
            java.io.IOException
    {
        //get Resource Specifier from XML file
        XMLInputSource in =
                new XMLInputSource(System.getProperty("analysis.engine.descriptor",
                        "/home/akemisetti/ctakesresources/desc/MetaMapApiAE.xml"));
        AnalysisEngineDescription desc =
                UIMAFramework.getXMLParser().parseAnalysisEngineDescription(in);
        //create AE here
        this.ae = UIMAFramework.produceAnalysisEngine(desc);
        //create a JCas, given an Analysis Engine (ae)
        this.jcas = this.ae.newJCas();
    }

    public void displayResults(JCas aJCas) {
        FSIndex utteranceIndex = aJCas.getAnnotationIndex(Utterance.type);
        Iterator utteranceIter = utteranceIndex.iterator();
        while (utteranceIter.hasNext()) {
            Utterance utterance = (Utterance)utteranceIter.next();
            System.out.println("pmid:" + utterance.getPmid());
            System.out.println("location:" + utterance.getLocation());
            System.out.println("start:" + utterance.getStart());
            System.out.println("begin:" + utterance.getBegin());
            System.out.println("end:" + utterance.getEnd());
            System.out.println("text:" + utterance.getCoveredText());

            for (FeatureStructure phraseFeature: utterance.getPhrases().toArray()) {
                System.out.println("  phrase text:" + ((Phrase)phraseFeature).getCoveredText());
                System.out.println("  phrase start:" + ((Phrase)phraseFeature).getStart());
                System.out.println("  phrase begin:" + ((Phrase)phraseFeature).getBegin());
                System.out.println("  phrase end:" + ((Phrase)phraseFeature).getEnd());
                System.out.println("  phrase text:" + ((Phrase)phraseFeature).getCoveredText());
                for (FeatureStructure mappingFeature: ((Phrase)phraseFeature).getMappings().toArray()) {
                    System.out.println("    mapping score:" + ((Mapping)mappingFeature).getScore());
                    for (FeatureStructure candidateFeature: ((Mapping)mappingFeature).getCandidates().toArray()) {
                        System.out.println("      candidate start:" + ((Candidate)candidateFeature).getStart());
                        System.out.println("      candidate begin:" + ((Candidate)candidateFeature).getBegin());
                        System.out.println("      candidate end:" + ((Candidate)candidateFeature).getEnd());
                        System.out.println("      candidate text:" + ((Candidate)candidateFeature).getCoveredText());
                        System.out.println("      candidate score:" + ((Candidate)candidateFeature).getScore());
                        System.out.println("      candidate concept:" + ((Candidate)candidateFeature).getConcept());
                        System.out.println("      candidate cui:" + ((Candidate)candidateFeature).getCui());
                        System.out.println("      candidate head:" + ((Candidate)candidateFeature).getHead());
                        for (FeatureStructure spanFeature: ((Candidate)candidateFeature).getSpans().toArray()) {
                            System.out.println("       candidate span:" + ((Span)spanFeature).getBegin() + "," + ((Span)spanFeature).getEnd());
                        }
                    }
                }
            }
        }
    }

    public void process (String doc1text)
            throws org.apache.uima.cas.CASRuntimeException,
            org.apache.uima.analysis_engine.AnalysisEngineProcessException,
            org.apache.uima.resource.ResourceInitializationException,
            org.apache.uima.util.InvalidXMLException,
            java.io.IOException

    {
        //analyze a document
        this.jcas.setDocumentText(doc1text);
        ae.process(this.jcas);
        displayResults(this.jcas);
        this.jcas.reset();
    }

    public void release() {
        // ...
        //done
        this.ae.destroy();

    }

    public static void main(String[] args)
            throws org.apache.uima.cas.CASRuntimeException,
            org.apache.uima.analysis_engine.AnalysisEngineProcessException,
            org.apache.uima.resource.ResourceInitializationException,
            org.apache.uima.util.InvalidXMLException,
            java.io.IOException
    {
        UIMASampleApp app = new UIMASampleApp();
        String doc1 = "These variables accounted for 62% of the variance (58% adjusted) in adjustment when adjustment at diagnosis was controlled.";
        String doc2 = "PMID- 9426087\nTI  - Validity of subjective assessment of changes in respiratory health status: a 30 year epidemiological study of workers in Paris.\nAB  - The validity of scales used for subjective assessment of health, particularly transitional indices, is under discussion. The aim of the present study was to assess the concurrent and predictive validity of a simple estimate of long-term subjective assessment of respiratory health changes. A longitudinal study of 915 workers was conducted over 30 yrs, with both retrospective self-assessment of respiratory health changes and objective measurements of spirometric values 12 yrs apart. An assessment of the reason for death during the subsequent 20 yrs was performed. Subjective assessment of respiratory deterioration over 12 yrs was significantly related to both cross-sectional lung function values and longitudinal lung function changes (forced expiratory volume in one second (FEV1) decline), an association which remained after adjustment for FEV1 level. It was also related to the same risk factors as decline in FEV1 (smoking, occupational exposure). Self-evaluation of respiratory deterioration was significantly predictive of death from all causes, with the highest (but nonsignificant) rate ratio for respiratory causes. Asthmatics exhibited greater long-term variability (objective and subjective) than nonasthmatics. Independent of dyspnoea, self-assessment of respiratory health deterioration was significantly related to FEV1. Subjective assessment of long-term changes in respiratory health provides valid information.";
        //app.process(DBUtils.getSampleDischargeSummary());
        //app.process(doc2);
        app.release();
    }
}