package edu.usfca.hs619

import org.slf4j.{Logger,LoggerFactory}


/**
  * This is the main scala object where eventually all the apis would be wrapped up and a simple and consistent
  * api would be provided.
  *
  * Currently this framework offers apis for each persistent option like DBUtils.
  */

object SimpleNLP extends App {

  def main(): Unit = {

    val log = LoggerFactory.getLogger(this.getClass)
    log.info("######################### Start Processing ##########################")
    // All the utils would be wrapped up here.
    log.info("Common invocation stategy coming soon ....")

  }



}