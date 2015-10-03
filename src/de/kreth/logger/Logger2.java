package de.kreth.logger;

import org.apache.log4j.Logger;

public class Logger2 {

   private Logger log;
   public enum Level {
      /**
       * Level 0: Kein Logging.
       */
      OFF,
      /**
       * Level 100: Nur fatale Fehler, die Programmabbruch bedingen.
       */
      FATAL,
      /**
       * Level 200: Alle Fehler.
       */
      ERROR,
      /**
       * Level 300: Alle Fehler und Warnungen.
       */
      WARN,
      /**
       * Level 400: Ausführliches Logging.
       */
      INFO,
      /**
       * Level 500: Sehr viele Ablauf Informationen - Debugging.
       */
      DEBUG,
      /**
       * Level 600: Fast alles loggen
       */
      TRACE,
      /**
       * Level 1000: Alles loggen.
       */
      ALL, 
   }

   public Logger2(Class<?> clazz) {
      log = Logger.getLogger(clazz);
   }

   public Logger2(String name) {
      log = Logger.getLogger(name);
   }

   public void setLevel(Level level) {
      log.setLevel(org.apache.log4j.Level.toLevel(level.name()));
   }
   
   public Level getCurrentLevel() {
      return Level.valueOf(log.getLevel().toString());
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
