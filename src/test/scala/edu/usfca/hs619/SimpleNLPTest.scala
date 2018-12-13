package edu.usfca.hs619

import edu.usfca.hs619.builders.MetamapPipelineBuilder
import edu.usfca.hs619.util.{DBUtils, ProcessUtils}
import org.json4s.JsonAST.JValue
import org.scalatest.FunSuite

/**
  * This is a test suite to test all the important methods of the framework
  */
class SimpleNLPTest extends FunSuite {

  /**
    * Test the reading of the Config File.
    */
  test("reading Config File"){
        assert(DBUtils.readConfig().isInstanceOf[JValue])
     }

  /**
    * Testing the sample discharge
    */
  test("Getting Only a Single Dischange Text"){
       val t = DBUtils.getSampleDischargeSummary()
       assert(DBUtils.getSampleDischargeSummary().size === 21748)
     }

  /**
    *  Testing the DB metadata from the Dischage Summary
    */
  test("Getting DB metadata from Discharge Summary"){
        assert(ProcessUtils.getRecordMetaData(DBUtils.getSampleDischargeSummary()) === "8,8674,121938")
     }

  /**
    * Testing processing two dischage summaries
    */
  test("Processing two Discharge Summaries with Default options"){
       assert(DBUtils.processDischargeSummaries(cnt = 2) === "Success")
     }

  /**
    * Testing processing the sample discharge summary
    */
  test("Processing the Sample Discharge Summary"){
       DBUtils.processDischargeSummaryText()
     }

  /**
    * Testing truncating the tables
    */
  test("Trucating all the tables"){
       assert(DBUtils.truncateTables() === "Success")
     }

  /**
    * Testing the discharge summary using metamap
    */
  test("Testing Sample Discharge Summary for Metamap"){
       DBUtils.processDischargeSummaryText(builder = MetamapPipelineBuilder)
     }

  /**
    * Testing two discharge summaries using metamap.
    */
  test("Processing two Discharge Summaries with metamap"){
      assert(DBUtils.processDischargeSummaries(builder = MetamapPipelineBuilder, cnt = 2) === "Success")
    }

}
