! function(a) {
  "function" == typeof define && define.amd ? define(["jquery", "moment"], a) : "object" == typeof exports ? module.exports = a(require("jquery"), require("moment")) : a(jQuery, moment)
}(function(a, b) {
  ! function() {
    "use strict";
    var a = "Jan._Feb._Mrt._Apr._Mei_Jun._Jul._Aug._Sep._okt._Nov._Dec.".split("_"),
        c = "Jan_Feb_Mrt_Apr_Mei_Jun_Jul_Aug_Sep_okt_Nov_Dec".split("_"),
        d = (b.defineLocale || b.lang).call(b, "nl", {
          months: "Januari_Februari_Maart_April_Mei_Juni_Juli_Augustus_September_Oktober_November_December".split("_"),
          monthsShort: function(b, d) {
            return /-MMM-/.test(d) ? c[b.month()] : a[b.month()]
          },
          monthsParseExact: !0,
          weekdays: "Zondag_Maandag_Dinsdag_Woensdag_Donderdag_Vrijdag_Zaterdag".split("_"),
          weekdaysShort: "Zo._Ma._Di._Wo._Do._Vr._Za.".split("_"),
          weekdaysMin: "Zo_Ma_Di_Wo_Do_Vr_Za".split("_"),
          weekdaysParseExact: !0,
          longDateFormat: {
            LT: "HH:mm",
            LTS: "HH:mm:ss",
            L: "DD-MM-YYYY",
            LL: "D MMMM YYYY",
            LLL: "D MMMM YYYY HH:mm",
            LLLL: "dddd D MMMM YYYY HH:mm"
          },
          calendar: {
            sameDay: "[vandaag om] LT",
            nextDay: "[morgen om] LT",
            nextWeek: "dddd [om] LT",
            lastDay: "[gisteren om] LT",
            lastWeek: "[afgelopen] dddd [om] LT",
            sameElse: "L"
          },
          relativeTime: {
            future: "over %s",
            past: "%s geleden",
            s: "een paar seconden",
            m: "één minuut",
            mm: "%d minuten",
            h: "één uur",
            hh: "%d uur",
            d: "één dag",
            dd: "%d dagen",
            M: "één maand",
            MM: "%d maanden",
            y: "één jaar",
            yy: "%d jaar"
          },
          ordinalParse: /\d{1,2}(ste|de)/,
          ordinal: function(a) {
            return a + (1 === a || 8 === a || a >= 20 ? "ste" : "de")
          },
          week: {
            dow: 1,
            doy: 4
          }
        });
    return d
  }(), a.fullCalendar.datepickerLang("nl", "nl", {
    closeText: "Sluiten",
    prevText: "←",
    nextText: "→",
    currentText: "Vandaag",
    monthNames: ["Januari", "Februari", "Maart", "April", "Mei", "Juni", "Juli", "Augustus", "September", "Oktober", "November", "December"],
    monthNamesShort: ["Jan", "Feb", "Mrt", "Apr", "Mei", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"],
    dayNames: ["Zondag", "Maandag", "Dinsdag", "Woensdag", "Donderdag", "Vrijdag", "Zaterdag"],
    dayNamesShort: ["Zon", "Maa", "Din", "Woe", "Don", "Vri", "Zat"],
    dayNamesMin: ["Zo", "Ma", "Di", "Wo", "Do", "Vr", "Za"],
    weekHeader: "Wk",
    dateFormat: "dd-mm-yy",
    firstDay: 1,
    isRTL: !1,
    showMonthAfterYear: !1,
    yearSuffix: ""
  }), a.fullCalendar.lang("nl", {
    buttonText: {
      month: "Maand",
      week: "Week",
      day: "Dag",
      list: "Agenda"
    },
    allDayText: "Hele dag",
    eventLimitText: "extra"
  })
});