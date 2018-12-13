package edu.usfca.hs619.samples;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;

import java.lang.Exception;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import edu.usfca.hs619.util.DBUtils;
import gov.nih.nlm.nls.metamap.MetaMapApi;
import gov.nih.nlm.nls.metamap.MetaMapApiImpl;
import gov.nih.nlm.nls.metamap.Result;
import gov.nih.nlm.nls.metamap.Utterance;
import gov.nih.nlm.nls.metamap.PCM;
import gov.nih.nlm.nls.metamap.Mapping;
import gov.nih.nlm.nls.metamap.Ev;
import org.metamap.uima.ts.Candidate;

public class CUIListExample {
    /**
     * MetaMap api instance
     */
    MetaMapApi api;
    long rrTime = 0;
    long eTime = 0;

    /**
     * Creates a new <code>CUIListExample</code> instance.
     */
    public CUIListExample() {
        this.api = new MetaMapApiImpl();
    }

    Set<String> process(String text)
            throws Exception {
        Set<String> cuiSet = new HashSet<String>();
        List<String> theOptions = new ArrayList<String>();
        theOptions.add("-y"); // Use WSD
        theOptions.add("-c"); // show candidates
        theOptions.add("-s"); // short semantic types

        theOptions.add("-I"); // show CUIs
        theOptions.add("--negex"); // enable negation

        theOptions.add("-R"); // Restrict to Sources
        theOptions.add("SNOMEDCT_US,RXNORM,ICD10CM,LNC,NDFRT,RXNORM"); // No space between arguments
        theOptions.add("-J"); // Restrict to semantic types
        theOptions.add("aggp,bpoc,clna,clnd,diap,dsyn,fndg,hlca,lbpr,lbtr,medd,menp,mobd,orga,orgf,phsu,qlco,qnco,socb,sosy,tmco,topp,vita"); // No space between arguments
        theOptions.add("--UDA");
        theOptions.add("/home/akemisetti/uda.txt");

        if (theOptions.size() > 0)
        {
            api.setOptions(theOptions);
        }
        // System.out.println("options: " + api.getOptions());
        if (text.trim().length() > 0) {
            long startTime = System.currentTimeMillis();
            List<Result> resultList = api.processCitationsFromString(text);
            long endTime = System.currentTimeMillis();
            rrTime = rrTime + (endTime - startTime);
            startTime = System.currentTimeMillis();
            for (Result result : resultList) {
                  System.out.println("\n>>>>");
                  System.out.println("Number of negations are "+ result.getNegations().size());
                  System.out.println("Number of Arconym Abbrevations "+result.getAcronymsAbbrevs().size());
                  System.out.println("Input Text "+ result.getInputText());
                  System.out.println("Number of utternace list " + result.getUtteranceList().size());

                if (result != null) {

                    /** Add cuis in evterms to cui set */
                    for (Utterance utterance : result.getUtteranceList()) {
                         System.out.println("Displaying Utterance " + utterance);
                         System.out.println("Number of PCMList "+utterance.getPCMList().size());
                        for (PCM pcm : utterance.getPCMList()) {
                             System.out.println("Number of Candidates "+pcm.getCandidateList().size());
                             System.out.println("Number of Mapping list "+pcm.getMappingList().size());
                             System.out.println("Candidates....");
//                            for (Ev mapEv : pcm.getCandidateList()) {
//                                cuiSet.add(mapEv.getConceptId());
//                                System.out.println(mapEv.getSources());
//                                System.out.println(mapEv.getSemanticTypes());
//                                System.out.println(mapEv.getConceptId());
//                                System.out.println(mapEv.getConceptName());
//                                System.out.println("-----\n");
//
//                            }
                            System.out.println("Mappings....");
                            for (Mapping map : pcm.getMappingList()) {
                                for (Ev mapEv : map.getEvList()) {
                                    cuiSet.add(mapEv.getConceptId());
                                    System.out.println(mapEv.getSources());
                                    System.out.println(mapEv.getSemanticTypes());
                                    System.out.println(mapEv.getConceptId());
                                    System.out.println(mapEv.getConceptName());
                                    System.out.println("-----\n");
                                }
                            }
                        }
                    }
                }
            }
            endTime = System.currentTimeMillis();
            eTime = eTime + (endTime - startTime);
        }
        return cuiSet;
    }

    public static void main(String[] args)
            throws Exception {
        if (1 == 1) {
            Set<String> cuiSet = new HashSet<String>();
            CUIListExample ceInstance = new CUIListExample();
//      BufferedReader br = new BufferedReader(new FileReader(args[0]));
//      String line;
//      while ((line = br.readLine()) != null) {
//	cuiSet.addAll(ceInstance.process(line));
//      }
//      br.close();
            /*ceInstance.process(DBUtils.getSampleDischargeSummary(new scala.Function0(121938)));
            for (String cui : cuiSet) {
                System.out.println(cui);
            }
            System.err.println("MetaMap server request/response time: " + ceInstance.rrTime + " milliseconds");
            System.err.println("Extracting response time: " + ceInstance.eTime + " milliseconds");*/

        } else {
            System.err.println("usage: example.CUIListExample inputfile");
        }
    }
}
