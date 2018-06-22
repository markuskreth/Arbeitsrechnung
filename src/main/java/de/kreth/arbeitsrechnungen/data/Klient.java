package de.kreth.arbeitsrechnungen.data;

/**
 * @author markus
 */
public class Klient {

   protected int klienten_id;
   protected String auftraggeber;
   protected String aEmail;
   protected String aTelefon;
   protected String aOrt;
   protected String aPlz;
   protected String aAdress2;
   protected String aAdress1;

   protected String kunde;
   protected String kEmail;
   protected String kTelefon;
   protected String kOrt;
   protected String kPlz;
   protected String kAdress2;
   protected String kAdress1;

   protected String bemerkungen;
   protected boolean zusatz1;
   protected String zusatz1_Name;
   protected boolean zusatz2;
   protected String zusatz2_Name;
   protected String rechnungnummer_bezeichnung;

   private Klient(Builder builder) {

      this.klienten_id = builder.klienten_id;
      this.auftraggeber = builder.auftraggeber;
      this.aEmail = builder.aEmail;
      this.aTelefon = builder.aTelefon;
      this.aOrt = builder.aOrt;
      this.aPlz = builder.aPlz;
      this.aAdress2 = builder.aAdress2;
      this.aAdress1 = builder.aAdress1;
      this.kunde = builder.kunde;
      this.kEmail = builder.kEmail;
      this.kTelefon = builder.kTelefon;
      this.kOrt = builder.kOrt;
      this.kPlz = builder.kPlz;
      this.kAdress2 = builder.kAdress2;
      this.kAdress1 = builder.kAdress1;
      this.bemerkungen = builder.bemerkungen;
      this.zusatz1 = builder.zusatz1;
      this.zusatz1_Name = builder.zusatz1_Name;
      this.zusatz2 = builder.zusatz2;
      this.zusatz2_Name = builder.zusatz2_Name;
      this.rechnungnummer_bezeichnung = builder.rechnungnummer_bezeichnung;
   }

   public String getKunde() {
      return kunde;
   }

   public String getAEmail() {
      return aEmail;
   }

   public String getATelefon() {
      return aTelefon;
   }

   public String getAOrt() {
      return aOrt;
   }

   public String getAPlz() {
      return aPlz;
   }

   public String getAAdress2() {
      return aAdress2;
   }

   public String getAAdress1() {
      return aAdress1;
   }

   public String getAuftraggeber() {
      return auftraggeber;
   }

   public String getKEmail() {
      return kEmail;
   }

   public String getKTelefon() {
      return kTelefon;
   }

   public String getKOrt() {
      return kOrt;
   }

   public String getKPlz() {
      return kPlz;
   }

   public String getKAdress2() {
      return kAdress2;
   }

   public String getKAdress1() {
      return kAdress1;
   }

   public int getKlienten_id() {
      return klienten_id;
   }

   public String getBemerkungen() {
      return bemerkungen;
   }

   public boolean hasZusatz1() {
      return zusatz1;
   }

   public String getZusatz1_Name() {
      return zusatz1_Name;
   }

   public boolean hasZusatz2() {
      return zusatz2;
   }

   public String getZusatz2_Name() {
      return zusatz2_Name;
   }

   public String getRechnungnummer_bezeichnung() {
      return rechnungnummer_bezeichnung;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((aAdress1 == null) ? 0 : aAdress1.hashCode());
      result = prime * result + ((aAdress2 == null) ? 0 : aAdress2.hashCode());
      result = prime * result + ((aEmail == null) ? 0 : aEmail.hashCode());
      result = prime * result + ((aOrt == null) ? 0 : aOrt.hashCode());
      result = prime * result + ((aPlz == null) ? 0 : aPlz.hashCode());
      result = prime * result + ((aTelefon == null) ? 0 : aTelefon.hashCode());
      result = prime * result + ((auftraggeber == null) ? 0 : auftraggeber.hashCode());
      result = prime * result + ((bemerkungen == null) ? 0 : bemerkungen.hashCode());
      result = prime * result + ((kAdress1 == null) ? 0 : kAdress1.hashCode());
      result = prime * result + ((kAdress2 == null) ? 0 : kAdress2.hashCode());
      result = prime * result + ((kEmail == null) ? 0 : kEmail.hashCode());
      result = prime * result + ((kOrt == null) ? 0 : kOrt.hashCode());
      result = prime * result + ((kPlz == null) ? 0 : kPlz.hashCode());
      result = prime * result + ((kTelefon == null) ? 0 : kTelefon.hashCode());
      result = prime * result + klienten_id;
      result = prime * result + ((kunde == null) ? 0 : kunde.hashCode());
      result = prime * result + ((rechnungnummer_bezeichnung == null) ? 0 : rechnungnummer_bezeichnung.hashCode());
      result = prime * result + (zusatz1 ? 1231 : 1237);
      result = prime * result + ((zusatz1_Name == null) ? 0 : zusatz1_Name.hashCode());
      result = prime * result + (zusatz2 ? 1231 : 1237);
      result = prime * result + ((zusatz2_Name == null) ? 0 : zusatz2_Name.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Klient other = (Klient) obj;
      if (aAdress1 == null) {
         if (other.aAdress1 != null)
            return false;
      } else if (!aAdress1.equals(other.aAdress1))
         return false;
      if (aAdress2 == null) {
         if (other.aAdress2 != null)
            return false;
      } else if (!aAdress2.equals(other.aAdress2))
         return false;
      if (aEmail == null) {
         if (other.aEmail != null)
            return false;
      } else if (!aEmail.equals(other.aEmail))
         return false;
      if (aOrt == null) {
         if (other.aOrt != null)
            return false;
      } else if (!aOrt.equals(other.aOrt))
         return false;
      if (aPlz == null) {
         if (other.aPlz != null)
            return false;
      } else if (!aPlz.equals(other.aPlz))
         return false;
      if (aTelefon == null) {
         if (other.aTelefon != null)
            return false;
      } else if (!aTelefon.equals(other.aTelefon))
         return false;
      if (auftraggeber == null) {
         if (other.auftraggeber != null)
            return false;
      } else if (!auftraggeber.equals(other.auftraggeber))
         return false;
      if (bemerkungen == null) {
         if (other.bemerkungen != null)
            return false;
      } else if (!bemerkungen.equals(other.bemerkungen))
         return false;
      if (kAdress1 == null) {
         if (other.kAdress1 != null)
            return false;
      } else if (!kAdress1.equals(other.kAdress1))
         return false;
      if (kAdress2 == null) {
         if (other.kAdress2 != null)
            return false;
      } else if (!kAdress2.equals(other.kAdress2))
         return false;
      if (kEmail == null) {
         if (other.kEmail != null)
            return false;
      } else if (!kEmail.equals(other.kEmail))
         return false;
      if (kOrt == null) {
         if (other.kOrt != null)
            return false;
      } else if (!kOrt.equals(other.kOrt))
         return false;
      if (kPlz == null) {
         if (other.kPlz != null)
            return false;
      } else if (!kPlz.equals(other.kPlz))
         return false;
      if (kTelefon == null) {
         if (other.kTelefon != null)
            return false;
      } else if (!kTelefon.equals(other.kTelefon))
         return false;
      if (klienten_id != other.klienten_id)
         return false;
      if (kunde == null) {
         if (other.kunde != null)
            return false;
      } else if (!kunde.equals(other.kunde))
         return false;
      if (rechnungnummer_bezeichnung == null) {
         if (other.rechnungnummer_bezeichnung != null)
            return false;
      } else if (!rechnungnummer_bezeichnung.equals(other.rechnungnummer_bezeichnung))
         return false;
      if (zusatz1 != other.zusatz1)
         return false;
      if (zusatz1_Name == null) {
         if (other.zusatz1_Name != null)
            return false;
      } else if (!zusatz1_Name.equals(other.zusatz1_Name))
         return false;
      if (zusatz2 != other.zusatz2)
         return false;
      if (zusatz2_Name == null) {
         if (other.zusatz2_Name != null)
            return false;
      } else if (!zusatz2_Name.equals(other.zusatz2_Name))
         return false;
      return true;
   }

   public static class Builder implements de.kreth.arbeitsrechnungen.Builder<Klient> {

      private int klienten_id;
      private String auftraggeber;
      private String aEmail;
      private String aTelefon;
      private String aOrt;
      private String aPlz;
      private String aAdress2;
      private String aAdress1;

      private String kunde;
      private String kEmail;
      private String kTelefon;
      private String kOrt;
      private String kPlz;
      private String kAdress2;
      private String kAdress1;

      private String bemerkungen;
      private boolean zusatz1;
      private String zusatz1_Name;
      private boolean zusatz2;
      private String zusatz2_Name;
      private String rechnungnummer_bezeichnung;

      public Builder(int klient_id, String auftraggeber2, String aAdresse1, String plz, String ort) {
         this.klienten_id = klient_id;
         this.auftraggeber = auftraggeber2;
         this.aAdress1 = aAdresse1;
         this.aPlz = plz;
         this.aOrt = ort;
      }

      public Builder aEmail(String aEmail) {
         this.aEmail = aEmail;
         return this;
      }

      public Builder aTelefon(String aTelefon) {
         this.aTelefon = aTelefon;
         return this;
      }

      public Builder aAdress2(String aAdress2) {
         this.aAdress2 = aAdress2;
         return this;
      }

      public Builder kunde(String kunde) {
         this.kunde = kunde;
         return this;
      }

      public Builder kEmail(String kEmail) {
         this.kEmail = kEmail;
         return this;
      }

      public Builder kTelefon(String kTelefon) {
         this.kTelefon = kTelefon;
         return this;
      }

      public Builder kOrt(String kOrt) {
         this.kOrt = kOrt;
         return this;
      }

      public Builder kPlz(String kPlz) {
         this.kPlz = kPlz;
         return this;
      }

      public Builder kAdress2(String kAdress2) {
         this.kAdress2 = kAdress2;
         return this;
      }

      public Builder kAdress1(String kAdress1) {
         this.kAdress1 = kAdress1;
         return this;
      }

      public Builder bemerkungen(String bemerkungen) {
         this.bemerkungen = bemerkungen;
         return this;
      }

      public Builder zusatz1(boolean zusatz1) {
         this.zusatz1 = zusatz1;
         return this;
      }

      public Builder zusatz1_Name(String zusatz1_Name) {
         this.zusatz1_Name = zusatz1_Name;
         return this;
      }

      public Builder zusatz2(boolean zusatz2) {
         this.zusatz2 = zusatz2;
         return this;
      }

      public Builder zusatz2_Name(String zusatz2_Name) {
         this.zusatz2_Name = zusatz2_Name;
         return this;
      }

      public Builder rechnungnummer_bezeichnung(String rechnungnummer_bezeichnung) {
         this.rechnungnummer_bezeichnung = rechnungnummer_bezeichnung;
         return this;
      }

      @Override
      public Klient build() {
         return new Klient(this);
      }

   }

}
