/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.kreth.arbeitsrechnungen;

/**
 * @author markus
 */
public class Punkt {

   int a = 0;
   int b = 0;

   public Punkt(int a, int b) {
      this.a = a;
      this.b = b;
   }

   public Punkt() {}

   public void setA(int a) {
      this.a = a;
   }

   public void setB(int b) {
      this.b = b;
   }

   public int getA() {
      return this.a;
   }

   public int getB() {
      return this.b;
   }
}
