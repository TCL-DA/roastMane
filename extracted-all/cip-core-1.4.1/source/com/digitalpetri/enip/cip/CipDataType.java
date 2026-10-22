package com.digitalpetri.enip.cip;

import java.util.Optional;

public enum CipDataType {
   BOOL(193),
   SINT(194),
   INT(195),
   DINT(196),
   LINT(197),
   USINT(198),
   UINT(199),
   UDINT(200),
   ULINT(201),
   REAL(202),
   LREAL(203),
   STIME(204),
   DATE(205),
   TIME_OF_DAY(206),
   DATE_AND_TIME(207),
   STRING(208),
   BYTE(209),
   WORD(210),
   DWORD(211),
   LWORD(212),
   STRING2(213),
   FTIME(214),
   LTIME(215),
   ITIME(216),
   STRINGN(217),
   SHORT_STRING(218),
   TIME(219),
   EPATH(220),
   ENGUNIT(221),
   STRINGI(222);

   private final int code;

   private CipDataType(int code) {
      this.code = code;
   }

   public final int getCode() {
      return this.code;
   }

   public static Optional<CipDataType> fromCode(int code) {
      for (CipDataType dataType : values()) {
         if (dataType.getCode() == code) {
            return Optional.of(dataType);
         }
      }

      return Optional.empty();
   }
}
