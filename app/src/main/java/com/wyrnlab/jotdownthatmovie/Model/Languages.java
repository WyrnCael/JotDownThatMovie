package com.wyrnlab.jotdownthatmovie.Model;

import com.wyrnlab.jotdownthatmovie.Utils.SetTheLanguages;

import java.util.HashMap;

public class Languages {

    private static final Languages intanc = new Languages();

    public static HashMap<String, LanguageTranslations> languagesByISOCode = new HashMap<String, LanguageTranslations>();

    public Languages(){
        initLanguages();
    }

    public static Languages getSingleton(){
        if(intanc == null){
            return new Languages();
        } else {
            return new Languages();
        }
    }

    public static void initLanguages(){
        languagesByISOCode = new HashMap<String, LanguageTranslations>();
        languagesByISOCode.put("xx",new LanguageTranslations("No Language","No idioma"));
        languagesByISOCode.put("aa",new LanguageTranslations("Afar","Lejos"));
        languagesByISOCode.put("af",new LanguageTranslations("Afrikaans","Africaans"));
        languagesByISOCode.put("ak",new LanguageTranslations("Akan","Akan"));
        languagesByISOCode.put("an",new LanguageTranslations("Aragonese","Aragonés"));
        languagesByISOCode.put("as",new LanguageTranslations("Assamese","Assamese"));
        languagesByISOCode.put("av",new LanguageTranslations("Avaric","Avaric"));
        languagesByISOCode.put("ae",new LanguageTranslations("Avestan","Avestan"));
        languagesByISOCode.put("ay",new LanguageTranslations("Aymara","Aimara"));
        languagesByISOCode.put("az",new LanguageTranslations("Azerbaijani","Azerbaiyán"));
        languagesByISOCode.put("ba",new LanguageTranslations("Bashkir","Bashkir"));
        languagesByISOCode.put("bm",new LanguageTranslations("Bambara","Bambara"));
        languagesByISOCode.put("bi",new LanguageTranslations("Bislama","Bislama"));
        languagesByISOCode.put("bo",new LanguageTranslations("Tibetan","Tibetano"));
        languagesByISOCode.put("br",new LanguageTranslations("Breton","Bretón"));
        languagesByISOCode.put("ca",new LanguageTranslations("Catalan","Catalán"));
        languagesByISOCode.put("cs",new LanguageTranslations("Czech","Checo"));
        languagesByISOCode.put("ce",new LanguageTranslations("Chechen","Chechen"));
        languagesByISOCode.put("cu",new LanguageTranslations("Slavic","Eslavo"));
        languagesByISOCode.put("cv",new LanguageTranslations("Chuvash","Chuvashia"));
        languagesByISOCode.put("kw",new LanguageTranslations("Cornish","De Cornualles"));
        languagesByISOCode.put("co",new LanguageTranslations("Corsican","Corso"));
        languagesByISOCode.put("cr",new LanguageTranslations("Cree","Cree"));
        languagesByISOCode.put("cy",new LanguageTranslations("Welsh","Galés"));
        languagesByISOCode.put("da",new LanguageTranslations("Danish","Danés"));
        languagesByISOCode.put("de",new LanguageTranslations("German","Alemán"));
        languagesByISOCode.put("dv",new LanguageTranslations("Divehi","Divehi"));
        languagesByISOCode.put("dz",new LanguageTranslations("Dzongkha","Dzongkha"));
        languagesByISOCode.put("eo",new LanguageTranslations("Esperanto","Esperanto"));
        languagesByISOCode.put("et",new LanguageTranslations("Estonian","Estonia"));
        languagesByISOCode.put("eu",new LanguageTranslations("Basque","Vasco"));
        languagesByISOCode.put("fo",new LanguageTranslations("Faroese","Faroese"));
        languagesByISOCode.put("fj",new LanguageTranslations("Fijian","Fiyiano"));
        languagesByISOCode.put("fi",new LanguageTranslations("Finnish","Finlandés"));
        languagesByISOCode.put("fr",new LanguageTranslations("French","Francés"));
        languagesByISOCode.put("fy",new LanguageTranslations("Frisian","Frisio"));
        languagesByISOCode.put("ff",new LanguageTranslations("Fulah","Fulah"));
        languagesByISOCode.put("gd",new LanguageTranslations("Gaelic","Gaélico"));
        languagesByISOCode.put("ga",new LanguageTranslations("Irish","Irlandesa"));
        languagesByISOCode.put("gl",new LanguageTranslations("Galician","Gallego"));
        languagesByISOCode.put("gv",new LanguageTranslations("Manx","Lengua de la isla de Man"));
        languagesByISOCode.put("gn",new LanguageTranslations("Guarani","Guaraní"));
        languagesByISOCode.put("gu",new LanguageTranslations("Gujarati","Gujarati"));
        languagesByISOCode.put("ht",new LanguageTranslations("Haitian","Haitiano"));
        languagesByISOCode.put("ha",new LanguageTranslations("Hausa","Hausa"));
        languagesByISOCode.put("sh",new LanguageTranslations("Serbo-Croatian","Servocroata"));
        languagesByISOCode.put("hz",new LanguageTranslations("Herero","Herero"));
        languagesByISOCode.put("ho",new LanguageTranslations("Hiri Motu","Hiri motu"));
        languagesByISOCode.put("hr",new LanguageTranslations("Croatian","Croata"));
        languagesByISOCode.put("hu",new LanguageTranslations("Hungarian","Húngaro"));
        languagesByISOCode.put("ig",new LanguageTranslations("Igbo","Igbo"));
        languagesByISOCode.put("io",new LanguageTranslations("Ido","Hago"));
        languagesByISOCode.put("ii",new LanguageTranslations("Yi","Yi"));
        languagesByISOCode.put("iu",new LanguageTranslations("Inuktitut","Inuktitut"));
        languagesByISOCode.put("ie",new LanguageTranslations("Interlingue","Occidental"));
        languagesByISOCode.put("ia",new LanguageTranslations("Interlingua","Interlingua"));
        languagesByISOCode.put("id",new LanguageTranslations("Indonesian","Indonesio"));
        languagesByISOCode.put("ik",new LanguageTranslations("Inupiaq","Inupiak"));
        languagesByISOCode.put("is",new LanguageTranslations("Icelandic","Islandés"));
        languagesByISOCode.put("it",new LanguageTranslations("Italian","Italiano"));
        languagesByISOCode.put("jv",new LanguageTranslations("Javanese","Javanés"));
        languagesByISOCode.put("ja",new LanguageTranslations("Japanese","Japonés"));
        languagesByISOCode.put("kl",new LanguageTranslations("Kalaallisut","Groenlandés"));
        languagesByISOCode.put("kn",new LanguageTranslations("Kannada","Kannada"));
        languagesByISOCode.put("ks",new LanguageTranslations("Kashmiri","Cachemira"));
        languagesByISOCode.put("kr",new LanguageTranslations("Kanuri","Kanuri"));
        languagesByISOCode.put("kk",new LanguageTranslations("Kazakh","Kazaja"));
        languagesByISOCode.put("km",new LanguageTranslations("Khmer","Khmer"));
        languagesByISOCode.put("ki",new LanguageTranslations("Kikuyu","Kikuyu"));
        languagesByISOCode.put("rw",new LanguageTranslations("Kinyarwanda","Kinyarwanda"));
        languagesByISOCode.put("ky",new LanguageTranslations("Kirghiz","Kirguises"));
        languagesByISOCode.put("kv",new LanguageTranslations("Komi","Komi"));
        languagesByISOCode.put("kg",new LanguageTranslations("Kongo","Kongo"));
        languagesByISOCode.put("ko",new LanguageTranslations("Korean","Coreano"));
        languagesByISOCode.put("kj",new LanguageTranslations("Kuanyama","Kuanyama"));
        languagesByISOCode.put("ku",new LanguageTranslations("Kurdish","Kurdo"));
        languagesByISOCode.put("lo",new LanguageTranslations("Lao","Lao"));
        languagesByISOCode.put("la",new LanguageTranslations("Latin","Latín"));
        languagesByISOCode.put("lv",new LanguageTranslations("Latvian","Letón"));
        languagesByISOCode.put("li",new LanguageTranslations("Limburgish","Limburgués"));
        languagesByISOCode.put("ln",new LanguageTranslations("Lingala","Lingala"));
        languagesByISOCode.put("lt",new LanguageTranslations("Lithuanian","Lituano"));
        languagesByISOCode.put("lb",new LanguageTranslations("Letzeburgesch","Letzeburgesch"));
        languagesByISOCode.put("lu",new LanguageTranslations("Luba-Katanga","Luba-Katanga"));
        languagesByISOCode.put("lg",new LanguageTranslations("Ganda","Ganda"));
        languagesByISOCode.put("mh",new LanguageTranslations("Marshall","Marshall"));
        languagesByISOCode.put("ml",new LanguageTranslations("Malayalam","Malayalam"));
        languagesByISOCode.put("mr",new LanguageTranslations("Marathi","Marathi"));
        languagesByISOCode.put("mg",new LanguageTranslations("Malagasy","Madagascarí"));
        languagesByISOCode.put("mt",new LanguageTranslations("Maltese","Maltés"));
        languagesByISOCode.put("mo",new LanguageTranslations("Moldavian","Moldavo"));
        languagesByISOCode.put("mn",new LanguageTranslations("Mongolian","Mongol"));
        languagesByISOCode.put("mi",new LanguageTranslations("Maori","Maorí"));
        languagesByISOCode.put("ms",new LanguageTranslations("Malay","Malayo"));
        languagesByISOCode.put("my",new LanguageTranslations("Burmese","Birmano"));
        languagesByISOCode.put("na",new LanguageTranslations("Nauru","Nauru"));
        languagesByISOCode.put("nv",new LanguageTranslations("Navajo","Navajo"));
        languagesByISOCode.put("nr",new LanguageTranslations("Ndebele","Ndebele"));
        languagesByISOCode.put("nd",new LanguageTranslations("Ndebele","Ndebele"));
        languagesByISOCode.put("ng",new LanguageTranslations("Ndonga","Ndonga"));
        languagesByISOCode.put("ne",new LanguageTranslations("Nepali","Nepalí"));
        languagesByISOCode.put("nl",new LanguageTranslations("Dutch","Holandés"));
        languagesByISOCode.put("nn",new LanguageTranslations("Norwegian Nynorsk","Nynorsk"));
        languagesByISOCode.put("nb",new LanguageTranslations("Norwegian Bokmål","Noruego"));
        languagesByISOCode.put("no",new LanguageTranslations("Norwegian","Noruego"));
        languagesByISOCode.put("ny",new LanguageTranslations("Chichewa","Chichewa"));
        languagesByISOCode.put("oc",new LanguageTranslations("Occitan","Occitano"));
        languagesByISOCode.put("oj",new LanguageTranslations("Ojibwa","Ojibwa"));
        languagesByISOCode.put("or",new LanguageTranslations("Oriya","Oriya"));
        languagesByISOCode.put("om",new LanguageTranslations("Oromo","Oromo"));
        languagesByISOCode.put("os",new LanguageTranslations("Ossetian","Osetia"));
        languagesByISOCode.put("pi",new LanguageTranslations("Pali","Pali"));
        languagesByISOCode.put("pl",new LanguageTranslations("Polish","Polaco"));
        languagesByISOCode.put("pt",new LanguageTranslations("Portuguese","Portugués"));
        languagesByISOCode.put("qu",new LanguageTranslations("Quechua","Quechua"));
        languagesByISOCode.put("rm",new LanguageTranslations("Raeto-Romance","Retorromance"));
        languagesByISOCode.put("ro",new LanguageTranslations("Romanian","Rumano"));
        languagesByISOCode.put("rn",new LanguageTranslations("Rundi","Kiroundi"));
        languagesByISOCode.put("ru",new LanguageTranslations("Russian","Ruso"));
        languagesByISOCode.put("sg",new LanguageTranslations("Sango","Sango"));
        languagesByISOCode.put("sa",new LanguageTranslations("Sanskrit","Sánscrito"));
        languagesByISOCode.put("si",new LanguageTranslations("Sinhalese","Sinhalese"));
        languagesByISOCode.put("sk",new LanguageTranslations("Slovak","Eslovaco"));
        languagesByISOCode.put("sl",new LanguageTranslations("Slovenian","Esloveno"));
        languagesByISOCode.put("se",new LanguageTranslations("Northern Sami","Sami septentrional"));
        languagesByISOCode.put("sm",new LanguageTranslations("Samoan","Samoano"));
        languagesByISOCode.put("sn",new LanguageTranslations("Shona","Shona"));
        languagesByISOCode.put("sd",new LanguageTranslations("Sindhi","Sindhi"));
        languagesByISOCode.put("so",new LanguageTranslations("Somali","Somalí"));
        languagesByISOCode.put("st",new LanguageTranslations("Sotho","Sotho"));
        languagesByISOCode.put("es",new LanguageTranslations("Spanish","Español"));
        languagesByISOCode.put("sq",new LanguageTranslations("Albanian","Albanés"));
        languagesByISOCode.put("sc",new LanguageTranslations("Sardinian","Sardo"));
        languagesByISOCode.put("sr",new LanguageTranslations("Serbian","Serbio"));
        languagesByISOCode.put("ss",new LanguageTranslations("Swati","Swati"));
        languagesByISOCode.put("su",new LanguageTranslations("Sundanese","Sundanese"));
        languagesByISOCode.put("sw",new LanguageTranslations("Swahili","Swahili"));
        languagesByISOCode.put("sv",new LanguageTranslations("Swedish","Sueco"));
        languagesByISOCode.put("ty",new LanguageTranslations("Tahitian","Tahití"));
        languagesByISOCode.put("ta",new LanguageTranslations("Tamil","Tamil"));
        languagesByISOCode.put("tt",new LanguageTranslations("Tatar","Tártaro"));
        languagesByISOCode.put("te",new LanguageTranslations("Telugu","Telugu"));
        languagesByISOCode.put("tg",new LanguageTranslations("Tajik","Tayiko"));
        languagesByISOCode.put("tl",new LanguageTranslations("Tagalog","Tagalo"));
        languagesByISOCode.put("th",new LanguageTranslations("Thai","Tailandés"));
        languagesByISOCode.put("ti",new LanguageTranslations("Tigrinya","Tigriño"));
        languagesByISOCode.put("to",new LanguageTranslations("Tonga","Tonga"));
        languagesByISOCode.put("tn",new LanguageTranslations("Tswana","Tsuana"));
        languagesByISOCode.put("ts",new LanguageTranslations("Tsonga","Tsonga"));
        languagesByISOCode.put("tk",new LanguageTranslations("Turkmen","Turkmen"));
        languagesByISOCode.put("tr",new LanguageTranslations("Turkish","Turco"));
        languagesByISOCode.put("tw",new LanguageTranslations("Twi","Twi"));
        languagesByISOCode.put("ug",new LanguageTranslations("Uighur","Uigur"));
        languagesByISOCode.put("uk",new LanguageTranslations("Ukrainian","Ucranio"));
        languagesByISOCode.put("ur",new LanguageTranslations("Urdu","Urdu"));
        languagesByISOCode.put("uz",new LanguageTranslations("Uzbek","Uzbeko"));
        languagesByISOCode.put("ve",new LanguageTranslations("Venda","Venda"));
        languagesByISOCode.put("vi",new LanguageTranslations("Vietnamese","Vietnamita"));
        languagesByISOCode.put("vo",new LanguageTranslations("Volapük","Volapük"));
        languagesByISOCode.put("wa",new LanguageTranslations("Walloon","Valonia"));
        languagesByISOCode.put("wo",new LanguageTranslations("Wolof","Wolof"));
        languagesByISOCode.put("xh",new LanguageTranslations("Xhosa","Xhosa"));
        languagesByISOCode.put("yi",new LanguageTranslations("Yiddish","Yídish"));
        languagesByISOCode.put("za",new LanguageTranslations("Zhuang","Zhuang"));
        languagesByISOCode.put("zu",new LanguageTranslations("Zulu","Zulú"));
        languagesByISOCode.put("ab",new LanguageTranslations("Abkhazian","Abjasia"));
        languagesByISOCode.put("zh",new LanguageTranslations("Mandarin","Mandarín"));
        languagesByISOCode.put("ps",new LanguageTranslations("Pushto","Pushto"));
        languagesByISOCode.put("am",new LanguageTranslations("Amharic","Amárico"));
        languagesByISOCode.put("ar",new LanguageTranslations("Arabic","Arábica"));
        languagesByISOCode.put("bg",new LanguageTranslations("Bulgarian","Búlgaro"));
        languagesByISOCode.put("cn",new LanguageTranslations("Cantonese","Cantonés"));
        languagesByISOCode.put("mk",new LanguageTranslations("Macedonian","Macedónio"));
        languagesByISOCode.put("el",new LanguageTranslations("Greek","Griego"));
        languagesByISOCode.put("fa",new LanguageTranslations("Persian","Persa"));
        languagesByISOCode.put("he",new LanguageTranslations("Hebrew","Hebreo"));
        languagesByISOCode.put("hi",new LanguageTranslations("Hindi","Hindi"));
        languagesByISOCode.put("hy",new LanguageTranslations("Armenian","Armenio"));
        languagesByISOCode.put("en",new LanguageTranslations("English","Inglés"));
        languagesByISOCode.put("ee",new LanguageTranslations("Ewe","Oveja"));
        languagesByISOCode.put("ka",new LanguageTranslations("Georgian","Georgiano"));
        languagesByISOCode.put("pa",new LanguageTranslations("Punjabi","Punjabi"));
        languagesByISOCode.put("bn",new LanguageTranslations("Bengali","Bengalí"));
        languagesByISOCode.put("bs",new LanguageTranslations("Bosnian","Bosnio"));
        languagesByISOCode.put("ch",new LanguageTranslations("Chamorro","Chamorro"));
        languagesByISOCode.put("be",new LanguageTranslations("Belarusian","Bielorruso"));
        languagesByISOCode.put("yo",new LanguageTranslations("Yoruba","Yoruba"));
    }

    public static String getLanguageName(String isoCode){
        if( SetTheLanguages.getLanguage().substring(0,2).equalsIgnoreCase("es") ){
            return languagesByISOCode.get(isoCode.toLowerCase()).getSpanish();
        } else {
            return languagesByISOCode.get(isoCode.toLowerCase()).getEnglish();
        }
    }

    public static class LanguageTranslations{
        public String english;
        public String spanish;

        public LanguageTranslations(String english, String spanish) {
            this.english = english;
            this.spanish = spanish;
        }

        public String getEnglish() {
            return english;
        }

        public void setEnglish(String english) {
            this.english = english;
        }

        public String getSpanish() {
            return spanish;
        }

        public void setSpanish(String spanish) {
            this.spanish = spanish;
        }
    }
}
