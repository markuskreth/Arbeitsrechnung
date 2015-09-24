package de.kreth.logger;

import org.apache.log4j.Logger;


public class Logger2 {

   Logger log;
   
   public Logger2(Class<?> clazz) {
      log = Logger.getLogger(clazz);
   }

   public Logger2(String name) {
      log = Logger.getLogger(name);
   }

   public void trace(Object message) {
      log.trace(message);
   }

   public void trace(Object message, Throwable t) {
      log.trace(message, t);
   }

   public void debug(Object message) {
      log.debug(message);
   }

   public void debug(Object message, Throwable t) {
      log.debug(message, t);
   }

   public void error(Object message) {
      log.error(message);
   }

   public void error(Object message, Throwable t) {
      log.error(message, t);
   }

   public void fatal(Object message) {
      log.fatal(message);
   }

   public void fatal(Object message, Throwable t) {
      log.fatal(message, t);
   }

   public void info(Object message) {
      log.info(message);
   }

   public void info(Object message, Throwable t) {
      log.info(message, t);
   }

   public void warn(Object message) {
      log.warn(message);
   }

   public void warn(Object message, Throwable t) {
      log.warn(message, t);
   }
   
}
