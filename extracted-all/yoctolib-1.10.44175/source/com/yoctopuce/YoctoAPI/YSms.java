package com.yoctopuce.YoctoAPI;

import java.util.ArrayList;
import java.util.Locale;

public class YSms {
   protected YMessageBox _mbox;
   protected int _slot = 0;
   protected boolean _deliv;
   protected String _smsc;
   protected int _mref = 0;
   protected String _orig;
   protected String _dest;
   protected int _pid = 0;
   protected int _alphab = 0;
   protected int _mclass = 0;
   protected String _stamp;
   protected byte[] _udh;
   protected byte[] _udata;
   protected int _npdu = 0;
   protected byte[] _pdu;
   protected ArrayList<YSms> _parts = new ArrayList<>();
   protected String _aggSig;
   protected int _aggIdx = 0;
   protected int _aggCnt = 0;

   protected YSms(YMessageBox mbox) {
      this._mbox = mbox;
   }

   public int get_slot() {
      return this._slot;
   }

   public String get_smsc() {
      return this._smsc;
   }

   public int get_msgRef() {
      return this._mref;
   }

   public String get_sender() {
      return this._orig;
   }

   public String get_recipient() {
      return this._dest;
   }

   public int get_protocolId() {
      return this._pid;
   }

   public boolean isReceived() {
      return this._deliv;
   }

   public int get_alphabet() {
      return this._alphab;
   }

   public int get_msgClass() {
      return (this._mclass & 16) == 0 ? -1 : this._mclass & 3;
   }

   public int get_dcs() {
      return this._mclass | this._alphab << 2;
   }

   public String get_timestamp() {
      return this._stamp;
   }

   public byte[] get_userDataHeader() {
      return this._udh;
   }

   public byte[] get_userData() {
      return this._udata;
   }

   public String get_textData() {
      if (this._alphab == 0) {
         return this._mbox.gsm2str(this._udata);
      } else if (this._alphab != 2) {
         return new String(this._udata);
      } else {
         int isosize = this._udata.length >> 1;
         byte[] isolatin = new byte[isosize];

         for (int i = 0; i < isosize; i++) {
            isolatin[i] = (byte)(this._udata[2 * i + 1] & 255 & 0xFF);
         }

         return new String(isolatin);
      }
   }

   public ArrayList<Integer> get_unicodeData() {
      ArrayList<Integer> res = new ArrayList<>();
      if (this._alphab == 0) {
         return this._mbox.gsm2unicode(this._udata);
      } else {
         if (this._alphab == 2) {
            int unisize = this._udata.length >> 1;
            res.clear();

            for (int i = 0; i < unisize; i++) {
               int unival = 256 * (this._udata[2 * i] & 255) + (this._udata[2 * i + 1] & 255);
               res.add(unival);
            }
         } else {
            int unisize = this._udata.length;
            res.clear();

            for (int i = 0; i < unisize; i++) {
               res.add((this._udata[i] & 255) + 0);
            }
         }

         return res;
      }
   }

   public int get_partCount() {
      if (this._npdu == 0) {
         this.generatePdu();
      }

      return this._npdu;
   }

   public byte[] get_pdu() {
      if (this._npdu == 0) {
         this.generatePdu();
      }

      return this._pdu;
   }

   public ArrayList<YSms> get_parts() {
      if (this._npdu == 0) {
         this.generatePdu();
      }

      return this._parts;
   }

   public String get_concatSignature() {
      if (this._npdu == 0) {
         this.generatePdu();
      }

      return this._aggSig;
   }

   public int get_concatIndex() {
      if (this._npdu == 0) {
         this.generatePdu();
      }

      return this._aggIdx;
   }

   public int get_concatCount() {
      if (this._npdu == 0) {
         this.generatePdu();
      }

      return this._aggCnt;
   }

   public int set_slot(int val) {
      this._slot = val;
      return 0;
   }

   public int set_received(boolean val) {
      this._deliv = val;
      return 0;
   }

   public int set_smsc(String val) {
      this._smsc = val;
      this._npdu = 0;
      return 0;
   }

   public int set_msgRef(int val) {
      this._mref = val;
      this._npdu = 0;
      return 0;
   }

   public int set_sender(String val) {
      this._orig = val;
      this._npdu = 0;
      return 0;
   }

   public int set_recipient(String val) {
      this._dest = val;
      this._npdu = 0;
      return 0;
   }

   public int set_protocolId(int val) {
      this._pid = val;
      this._npdu = 0;
      return 0;
   }

   public int set_alphabet(int val) {
      this._alphab = val;
      this._npdu = 0;
      return 0;
   }

   public int set_msgClass(int val) {
      if (val == -1) {
         this._mclass = 0;
      } else {
         this._mclass = 16 + val;
      }

      this._npdu = 0;
      return 0;
   }

   public int set_dcs(int val) {
      this._alphab = val >> 2 & 3;
      this._mclass = val & 19;
      this._npdu = 0;
      return 0;
   }

   public int set_timestamp(String val) {
      this._stamp = val;
      this._npdu = 0;
      return 0;
   }

   public int set_userDataHeader(byte[] val) {
      this._udh = val;
      this._npdu = 0;
      this.parseUserDataHeader();
      return 0;
   }

   public int set_userData(byte[] val) {
      this._udata = val;
      this._npdu = 0;
      return 0;
   }

   public int convertToUnicode() {
      ArrayList<Integer> ucs2 = new ArrayList<>();
      if (this._alphab == 2) {
         return 0;
      } else {
         if (this._alphab == 0) {
            ucs2 = this._mbox.gsm2unicode(this._udata);
         } else {
            int udatalen = this._udata.length;
            ucs2.clear();

            for (int i = 0; i < udatalen; i++) {
               int uni = this._udata[i] & 255;
               ucs2.add(uni);
            }
         }

         this._alphab = 2;
         this._udata = new byte[0];
         this.addUnicodeData(ucs2);
         return 0;
      }
   }

   public int addText(String val) {
      if (val.length() == 0) {
         return 0;
      } else {
         byte[] newdata;
         int newdatalen;
         if (this._alphab == 0) {
            newdata = this._mbox.str2gsm(val);
            newdatalen = newdata.length;
            if (newdatalen == 0) {
               this.convertToUnicode();
               newdata = val.getBytes();
               newdatalen = newdata.length;
            }
         } else {
            newdata = val.getBytes();
            newdatalen = newdata.length;
         }

         int udatalen = this._udata.length;
         byte[] udata;
         if (this._alphab == 2) {
            udata = new byte[udatalen + 2 * newdatalen];

            for (int i = 0; i < udatalen; i++) {
               udata[i] = (byte)(this._udata[i] & 255 & 0xFF);
            }

            for (int var7 = 0; var7 < newdatalen; var7++) {
               udata[udatalen + 1] = (byte)(newdata[var7] & 255 & 0xFF);
               udatalen += 2;
            }
         } else {
            udata = new byte[udatalen + newdatalen];

            for (int i = 0; i < udatalen; i++) {
               udata[i] = (byte)(this._udata[i] & 255 & 0xFF);
            }

            for (int var9 = 0; var9 < newdatalen; var9++) {
               udata[udatalen] = (byte)(newdata[var9] & 255 & 0xFF);
               udatalen++;
            }
         }

         return this.set_userData(udata);
      }
   }

   public int addUnicodeData(ArrayList<Integer> val) {
      if (this._alphab != 2) {
         this.convertToUnicode();
      }

      int arrlen = val.size();
      int newdatalen = arrlen;

      for (int i = 0; i < arrlen; i++) {
         int uni = val.get(i);
         if (uni > 65535) {
            newdatalen++;
         }
      }

      int udatalen = this._udata.length;
      byte[] udata = new byte[udatalen + 2 * newdatalen];

      for (int var9 = 0; var9 < udatalen; var9++) {
         udata[var9] = (byte)(this._udata[var9] & 255 & 0xFF);
      }

      for (int var10 = 0; var10 < arrlen; var10++) {
         int uni = val.get(var10);
         if (uni >= 65536) {
            int surrogate = uni - 65536;
            uni = (surrogate >> 10 & 1023) + 55296;
            udata[udatalen] = (byte)(uni >> 8 & 0xFF);
            udata[udatalen + 1] = (byte)(uni & 0xFF & 0xFF);
            udatalen += 2;
            uni = (surrogate & 1023) + 56320;
         }

         udata[udatalen] = (byte)(uni >> 8 & 0xFF);
         udata[udatalen + 1] = (byte)(uni & 0xFF & 0xFF);
         udatalen += 2;
      }

      return this.set_userData(udata);
   }

   public int set_pdu(byte[] pdu) {
      this._pdu = pdu;
      this._npdu = 1;
      return this.parsePdu(pdu);
   }

   public int set_parts(ArrayList<YSms> parts) {
      ArrayList<YSms> sorted = new ArrayList<>();
      this._npdu = parts.size();
      if (this._npdu == 0) {
         return -2;
      } else {
         sorted.clear();
         int partno = 0;

         while (partno < this._npdu) {
            int initpartno = partno;

            for (int i = 0; i < this._npdu; i++) {
               YSms subsms = parts.get(i);
               if (subsms.get_concatIndex() == partno) {
                  sorted.add(subsms);
                  partno++;
               }
            }

            if (initpartno == partno) {
               partno++;
            }
         }

         this._parts = sorted;
         this._npdu = sorted.size();
         YSms subsms = this._parts.get(0);
         int retcode = this.parsePdu(subsms.get_pdu());
         if (retcode != 0) {
            return retcode;
         } else {
            int totsize = 0;

            for (int var11 = 0; var11 < this._parts.size(); var11++) {
               subsms = this._parts.get(var11);
               byte[] subdata = subsms.get_userData();
               totsize += subdata.length;
            }

            byte[] res = new byte[totsize];
            totsize = 0;

            for (int var12 = 0; var12 < this._parts.size(); var12++) {
               subsms = this._parts.get(var12);
               byte[] subdata = subsms.get_userData();

               for (int ix = 0; ix < subdata.length; ix++) {
                  res[totsize] = (byte)(subdata[ix] & 255 & 0xFF);
                  totsize++;
               }
            }

            this._udata = res;
            return 0;
         }
      }
   }

   public byte[] encodeAddress(String addr) {
      byte[] bytes = addr.getBytes();
      int srclen = bytes.length;
      int numlen = 0;

      for (int i = 0; i < srclen; i++) {
         int val = bytes[i] & 255;
         if (val >= 48 && val < 58) {
            numlen++;
         }
      }

      if (numlen == 0) {
         return new byte[]{0};
      } else {
         byte[] res = new byte[2 + (numlen + 1 >> 1)];
         res[0] = (byte)(numlen & 0xFF);
         if ((bytes[0] & 255) == 43) {
            res[1] = -111;
         } else {
            res[1] = -127;
         }

         numlen = 4;
         int digit = 0;

         for (int var10 = 0; var10 < srclen; var10++) {
            int val = bytes[var10] & 255;
            if (val >= 48 && val < 58) {
               if ((numlen & 1) == 0) {
                  digit = val - 48;
               } else {
                  res[numlen >> 1] = (byte)(digit + 16 * (val - 48) & 0xFF);
               }

               numlen++;
            }
         }

         if ((numlen & 1) != 0) {
            res[numlen >> 1] = (byte)(digit + 240 & 0xFF);
         }

         return res;
      }
   }

   public String decodeAddress(byte[] addr, int ofs, int siz) {
      if (siz == 0) {
         return "";
      } else {
         String res = "";
         int addrType = addr[ofs] & 255 & 112;
         if (addrType == 80) {
            siz = 4 * siz / 7;
            byte[] gsm7 = new byte[siz];
            int rpos = 1;
            int carry = 0;
            int nbits = 0;

            for (int i = 0; i < siz; i++) {
               if (nbits == 7) {
                  gsm7[i] = (byte)(carry & 0xFF);
                  carry = 0;
                  nbits = 0;
               } else {
                  int byt = addr[ofs + rpos] & 255;
                  rpos++;
                  gsm7[i] = (byte)((carry | byt << nbits & 127) & 0xFF);
                  carry = byt >> 7 - nbits;
                  nbits++;
               }
            }

            return this._mbox.gsm2str(gsm7);
         } else {
            if (addrType == 16) {
               res = "+";
            }

            siz = siz + 1 >> 1;

            for (int ix = 0; ix < siz; ix++) {
               int byt = addr[ofs + ix + 1] & 255;
               res = String.format(Locale.US, "%s%x%x", res, byt & 15, byt >> 4);
            }

            if ((addr[ofs + siz] & 255) >> 4 == 15) {
               res = res.substring(0, res.length() - 1);
            }

            return res;
         }
      }
   }

   public byte[] encodeTimeStamp(String exp) {
      int explen = exp.length();
      if (explen == 0) {
         return new byte[0];
      } else if (exp.substring(0, 1).equals("+")) {
         int n = YAPIContext._atoi(exp.substring(1, 1 + explen - 1));
         byte[] res = new byte[1];
         if (n > 2592000) {
            n = 192 + (n + 518400) / 604800;
         } else if (n > 86400) {
            n = 166 + (n + 86399) / 86400;
         } else if (n > 43200) {
            n = 143 + (n - 43200 + 1799) / 1800;
         } else {
            n = -1 + (n + 299) / 300;
         }

         if (n < 0) {
            n = 0;
         }

         res[0] = (byte)(n & 0xFF);
         return res;
      } else {
         if (exp.substring(4, 5).equals("-") || exp.substring(4, 5).equals("/")) {
            exp = exp.substring(2, 2 + explen - 2);
            explen = exp.length();
         }

         byte[] expasc = exp.getBytes();
         byte[] resx = new byte[7];
         int nx = 0;

         int i;
         for (i = 0; i + 1 < explen && nx < 7; i++) {
            int v1 = expasc[i] & 255;
            if (v1 >= 48 && v1 < 58) {
               int v2 = expasc[i + 1] & 255;
               if (v2 >= 48 && v2 < 58) {
                  v1 -= 48;
                  v2 -= 48;
                  resx[nx] = (byte)((v2 << 4) + v1 & 0xFF);
                  nx++;
                  i++;
               }
            }
         }

         while (nx < 7) {
            resx[nx] = 0;
            nx++;
         }

         if (i + 2 < explen) {
            int v1 = expasc[i - 3] & 255;
            int v2 = expasc[i] & 255;
            if ((v1 == 43 || v1 == 45) && v2 == 58) {
               v1 = expasc[i + 1] & 255;
               v2 = expasc[i + 2] & 255;
               if (v1 >= 48 && v1 < 58 && v1 >= 48 && v1 < 58) {
                  v1 = (10 * (v1 - 48) + (v2 - 48)) / 15;
                  v2 = 4 * (resx[--nx] & 255) + v1;
                  if ((expasc[i - 3] & 255) == 45) {
                     v2 += 128;
                  }

                  resx[nx] = (byte)(v2 & 0xFF);
               }
            }
         }

         return resx;
      }
   }

   public String decodeTimeStamp(byte[] exp, int ofs, int siz) {
      if (siz < 1) {
         return "";
      } else if (siz == 1) {
         int n = exp[ofs] & 255;
         if (n < 144) {
            n *= 300;
         } else if (n < 168) {
            n = (n - 143) * 1800;
         } else if (n < 197) {
            n = (n - 166) * 86400;
         } else {
            n = (n - 192) * 7 * 86400;
         }

         return String.format(Locale.US, "+%d", n);
      } else {
         String res = "20";

         int i;
         for (i = 0; i < siz && i < 6; i++) {
            int byt = exp[ofs + i] & 255;
            res = String.format(Locale.US, "%s%x%x", res, byt & 15, byt >> 4);
            if (i < 3) {
               if (i < 2) {
                  res = String.format(Locale.US, "%s-", res);
               } else {
                  res = String.format(Locale.US, "%s ", res);
               }
            } else if (i < 5) {
               res = String.format(Locale.US, "%s:", res);
            }
         }

         if (siz == 7) {
            int byt = exp[ofs + i] & 255;
            String sign = "+";
            if ((byt & 8) != 0) {
               byt -= 8;
               sign = "-";
            }

            byt = 10 * (byt & 15) + (byt >> 4);
            String hh = String.format(Locale.US, "%d", byt >> 2);
            String ss = String.format(Locale.US, "%d", 15 * (byt & 3));
            if (hh.length() < 2) {
               hh = String.format(Locale.US, "0%s", hh);
            }

            if (ss.length() < 2) {
               ss = String.format(Locale.US, "0%s", ss);
            }

            res = String.format(Locale.US, "%s%s%s:%s", res, sign, hh, ss);
         }

         return res;
      }
   }

   public int udataSize() {
      int udhsize = this._udh.length;
      int res = this._udata.length;
      if (this._alphab == 0) {
         if (udhsize > 0) {
            res += (8 + 8 * udhsize + 6) / 7;
         }

         res = (res * 7 + 7) / 8;
      } else if (udhsize > 0) {
         res = res + 1 + udhsize;
      }

      return res;
   }

   public byte[] encodeUserData() {
      int udsize = this.udataSize();
      int udhsize = this._udh.length;
      int udlen = this._udata.length;
      byte[] res = new byte[1 + udsize];
      int udhlen = 0;
      int nbits = 0;
      int carry = 0;
      if (this._alphab == 0) {
         if (udhsize > 0) {
            udhlen = (8 + 8 * udhsize + 6) / 7;
            nbits = 7 * udhlen - 8 - 8 * udhsize;
         }

         res[0] = (byte)(udhlen + udlen & 0xFF);
      } else {
         res[0] = (byte)(udsize & 0xFF);
      }

      int wpos = 1;
      if (udhsize > 0) {
         res[wpos] = (byte)(udhsize & 0xFF);
         wpos++;

         for (int i = 0; i < udhsize; i++) {
            res[wpos] = (byte)(this._udh[i] & 255 & 0xFF);
            wpos++;
         }
      }

      if (this._alphab == 0) {
         for (int i = 0; i < udlen; i++) {
            if (nbits == 0) {
               carry = this._udata[i] & 255;
               nbits = 7;
            } else {
               int thi_b = this._udata[i] & 255;
               res[wpos] = (byte)((carry | thi_b << nbits & 0xFF) & 0xFF);
               wpos++;
               nbits--;
               carry = thi_b >> 7 - nbits;
            }
         }

         if (nbits > 0) {
            res[wpos] = (byte)(carry & 0xFF);
         }
      } else {
         for (int ix = 0; ix < udlen; ix++) {
            res[wpos] = (byte)(this._udata[ix] & 255 & 0xFF);
            wpos++;
         }
      }

      return res;
   }

   public int generateParts() {
      int udhsize = this._udh.length;
      int udlen = this._udata.length;
      int mss = 134 - udhsize;
      if (this._alphab == 0) {
         mss = (mss * 8 - 6) / 7;
      }

      this._npdu = (udlen + mss - 1) / mss;
      this._parts.clear();
      int partno = 0;
      int wpos = 0;

      while (wpos < udlen) {
         partno++;
         byte[] newudh = new byte[5 + udhsize];
         newudh[0] = 0;
         newudh[1] = 3;
         newudh[2] = (byte)(this._mref & 0xFF);
         newudh[3] = (byte)(this._npdu & 0xFF);
         newudh[4] = (byte)(partno & 0xFF);

         for (int i = 0; i < udhsize; i++) {
            newudh[5 + i] = (byte)(this._udh[i] & 255 & 0xFF);
         }

         int partlen;
         if (wpos + mss < udlen) {
            partlen = mss;
         } else {
            partlen = udlen - wpos;
         }

         byte[] newud = new byte[partlen];

         for (int var11 = 0; var11 < partlen; var11++) {
            newud[var11] = (byte)(this._udata[wpos] & 255 & 0xFF);
            wpos++;
         }

         YSms newpdu = new YSms(this._mbox);
         newpdu.set_received(this.isReceived());
         newpdu.set_smsc(this.get_smsc());
         newpdu.set_msgRef(this.get_msgRef());
         newpdu.set_sender(this.get_sender());
         newpdu.set_recipient(this.get_recipient());
         newpdu.set_protocolId(this.get_protocolId());
         newpdu.set_dcs(this.get_dcs());
         newpdu.set_timestamp(this.get_timestamp());
         newpdu.set_userDataHeader(newudh);
         newpdu.set_userData(newud);
         this._parts.add(newpdu);
      }

      return 0;
   }

   public int generatePdu() {
      this._parts.clear();
      if (this.udataSize() > 140) {
         this._pdu = new byte[0];
         return this.generateParts();
      } else {
         byte[] sca = this.encodeAddress(this._smsc);
         if (sca.length > 0) {
            sca[0] = (byte)(sca.length - 1 & 0xFF);
         }

         byte[] stamp = this.encodeTimeStamp(this._stamp);
         byte[] udata = this.encodeUserData();
         byte[] hdr;
         byte[] addr;
         int pdutyp;
         if (this._deliv) {
            addr = this.encodeAddress(this._orig);
            hdr = new byte[1];
            pdutyp = 0;
         } else {
            addr = this.encodeAddress(this._dest);
            this._mref = this._mbox.nextMsgRef();
            hdr = new byte[2];
            hdr[1] = (byte)(this._mref & 0xFF);
            pdutyp = 1;
            if (stamp.length > 0) {
               pdutyp += 16;
            }

            if (stamp.length == 7) {
               pdutyp += 8;
            }
         }

         if (this._udh.length > 0) {
            pdutyp += 64;
         }

         hdr[0] = (byte)(pdutyp & 0xFF);
         int pdulen = sca.length + hdr.length + addr.length + 2 + stamp.length + udata.length;
         this._pdu = new byte[pdulen];
         pdulen = 0;

         for (int i = 0; i < sca.length; i++) {
            this._pdu[pdulen] = (byte)(sca[i] & 255 & 0xFF);
            pdulen++;
         }

         for (int var12 = 0; var12 < hdr.length; var12++) {
            this._pdu[pdulen] = (byte)(hdr[var12] & 255 & 0xFF);
            pdulen++;
         }

         for (int var13 = 0; var13 < addr.length; var13++) {
            this._pdu[pdulen] = (byte)(addr[var13] & 255 & 0xFF);
            pdulen++;
         }

         this._pdu[pdulen] = (byte)(this._pid & 0xFF);
         pdulen++;
         this._pdu[pdulen] = (byte)(this.get_dcs() & 0xFF);
         pdulen++;

         for (int var14 = 0; var14 < stamp.length; var14++) {
            this._pdu[pdulen] = (byte)(stamp[var14] & 255 & 0xFF);
            pdulen++;
         }

         for (int var15 = 0; var15 < udata.length; var15++) {
            this._pdu[pdulen] = (byte)(udata[var15] & 255 & 0xFF);
            pdulen++;
         }

         this._npdu = 1;
         return 0;
      }
   }

   public int parseUserDataHeader() {
      this._aggSig = "";
      this._aggIdx = 0;
      this._aggCnt = 0;
      int udhlen = this._udh.length;
      int i = 0;

      while (i + 1 < udhlen) {
         int iei = this._udh[i] & 255;
         int ielen = this._udh[i + 1] & 255;
         i += 2;
         if (i + ielen <= udhlen) {
            if (iei == 0 && ielen == 3) {
               String sig = String.format(Locale.US, "%s-%s-%02x-%02x", this._orig, this._dest, this._mref, this._udh[i] & 255);
               this._aggSig = sig;
               this._aggCnt = this._udh[i + 1] & 255;
               this._aggIdx = this._udh[i + 2] & 255;
            }

            if (iei == 8 && ielen == 4) {
               String sig = String.format(Locale.US, "%s-%s-%02x-%02x%02x", this._orig, this._dest, this._mref, this._udh[i] & 255, this._udh[i + 1] & 255);
               this._aggSig = sig;
               this._aggCnt = this._udh[i + 2] & 255;
               this._aggIdx = this._udh[i + 3] & 255;
            }
         }

         i += ielen;
      }

      return 0;
   }

   public int parsePdu(byte[] pdu) {
      this._pdu = pdu;
      this._npdu = 1;
      this._smsc = this.decodeAddress(pdu, 1, 2 * ((pdu[0] & 255) - 1));
      int rpos = 1 + (pdu[0] & 255);
      int pdutyp = pdu[rpos] & 255;
      rpos++;
      this._deliv = (pdutyp & 3) == 0;
      int addrlen;
      int tslen;
      if (this._deliv) {
         addrlen = pdu[rpos] & 255;
         this._orig = this.decodeAddress(pdu, ++rpos, addrlen);
         this._dest = "";
         tslen = 7;
      } else {
         this._mref = pdu[rpos] & 255;
         addrlen = pdu[++rpos] & 255;
         this._dest = this.decodeAddress(pdu, ++rpos, addrlen);
         this._orig = "";
         if ((pdutyp & 16) != 0) {
            if ((pdutyp & 8) != 0) {
               tslen = 7;
            } else {
               tslen = 1;
            }
         } else {
            tslen = 0;
         }
      }

      rpos += addrlen + 3 >> 1;
      this._pid = pdu[rpos] & 255;
      int dcs = pdu[++rpos] & 255;
      rpos++;
      this._alphab = dcs >> 2 & 3;
      this._mclass = dcs & 19;
      this._stamp = this.decodeTimeStamp(pdu, rpos, tslen);
      rpos += tslen;
      int nbits = 0;
      int carry = 0;
      int udlen = pdu[rpos] & 255;
      rpos++;
      if ((pdutyp & 64) != 0) {
         int udhsize = pdu[rpos] & 255;
         rpos++;
         this._udh = new byte[udhsize];

         for (int i = 0; i < udhsize; i++) {
            this._udh[i] = (byte)(pdu[rpos] & 255 & 0xFF);
            rpos++;
         }

         int udhlen;
         if (this._alphab == 0) {
            udhlen = (8 + 8 * udhsize + 6) / 7;
            nbits = 7 * udhlen - 8 - 8 * udhsize;
            if (nbits > 0) {
               int thi_b = pdu[rpos] & 255;
               rpos++;
               carry = thi_b >> nbits;
               nbits = 8 - nbits;
            }
         } else {
            udhlen = 1 + udhsize;
         }

         udlen -= udhlen;
      } else {
         int udhsize = 0;
         this._udh = new byte[0];
      }

      this._udata = new byte[udlen];
      if (this._alphab == 0) {
         for (int i = 0; i < udlen; i++) {
            if (nbits == 7) {
               this._udata[i] = (byte)(carry & 0xFF);
               carry = 0;
               nbits = 0;
            } else {
               int thi_b = pdu[rpos] & 255;
               rpos++;
               this._udata[i] = (byte)((carry | thi_b << nbits & 127) & 0xFF);
               carry = thi_b >> 7 - nbits;
               nbits++;
            }
         }
      } else {
         for (int ix = 0; ix < udlen; ix++) {
            this._udata[ix] = (byte)(pdu[rpos] & 255 & 0xFF);
            rpos++;
         }
      }

      this.parseUserDataHeader();
      return 0;
   }

   public int send() throws YAPI_Exception {
      if (this._npdu == 0) {
         this.generatePdu();
      }

      if (this._npdu == 1) {
         return this._mbox._upload("sendSMS", this._pdu);
      } else {
         int retcode = 0;

         for (int i = 0; i < this._npdu && retcode == 0; i++) {
            YSms pdu = this._parts.get(i);
            retcode = pdu.send();
         }

         return retcode;
      }
   }

   public int deleteFromSIM() throws YAPI_Exception {
      if (this._slot > 0) {
         return this._mbox.clearSIMSlot(this._slot);
      } else {
         int retcode = 0;

         for (int i = 0; i < this._npdu && retcode == 0; i++) {
            YSms pdu = this._parts.get(i);
            retcode = pdu.deleteFromSIM();
         }

         return retcode;
      }
   }
}
