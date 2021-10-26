package no.difi.meldingsutveksling.domain;

import lombok.Getter;

import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum ICD {
    AD_VAT("9922", "AD:VAT", "Andorra VAT number"),
    AL_VAT("9923", "AL:VAT", "Albania VAT number"),
    AT_CID("9916", "AT:CID", "Firmenidentifikationsnummer der Statistik Austria"),
    AT_GOV("9915", "AT:GOV", "Österreichisches Verwaltungs bzw. Organisationskennzeichen"),
    AT_KUR("9919", "AT:KUR", "Kennziffer des Unternehmensregisters"),
    AT_VAT("9914", "AT:VAT", "Österreichische Umsatzsteuer-Identifikationsnummer"),
    BA_VAT("9924", "BA:VAT", "Bosnia and Herzegovina VAT number"),
    BE_CBE("9956", "BE:CBE", "Belgian Crossroad Bank of Enterprises"),
    BE_VAT("9925", "BE:VAT", "Belgium VAT number"),
    BG_VAT("9926", "BG:VAT", "Bulgaria VAT number"),
    CH_VAT("9927", "CH:VAT", "Switzerland VAT number"),
    CY_VAT("9928", "CY:VAT", "Cyprus VAT number"),
    CZ_VAT("9929", "CZ:VAT", "Czech Republic VAT number"),
    DE_LID("9958", "DE:LID", "German Leitweg ID"),
    DE_VAT("9930", "DE:VAT", "Germany VAT number"),
    DK_CPR("9901", "DK:CPR", "Danish Ministry of the Interior and Health"),
    DK_CVR("9902", "DK:CVR", "The Danish Commerce and Companies Agency"),
    DK_DIGST("0184", "DK:DIGST", "DIGSTORG"),
    DK_P("0096", "DK:P", "The Danish Commerce and Companies Agency"),
    DK_SE("9904", "DK:SE", "Danish Ministry of Taxation, Central Customs and Tax Administration"),
    DK_VANS("9905", "DK:VANS", "Danish VANS providers"),
    DUNS("0060", "DUNS", "Dun and Bradstreet Ltd"),
    EE_RIK("0191", "EE:RIK", "Estonia company code"),
    EE_VAT("9931", "EE:VAT", "Estonia VAT number"),
    ES_VAT("9920", "ES:VAT", "Agencia Española de Administración Tributaria"),
    EU_REID("9913", "EU:REID", "Business Registers Network"),
    EU_VAT("9912", "EU:VAT", "National ministries of Economy"),
    FI_OVT("0037", "FI:OVT", "Finnish tax board"),
    FR_SIRENE("0002", "FR:SIRENE", "INSEE: National Institute for statistics and Economic studies"),
    FR_SIRET("0009", "FR:SIRET", "DU PONT DE NEMOURS"),
    FR_VAT("9957", "FR:VAT", "France VAT number"),
    GB_VAT("9932", "GB:VAT", "United Kingdom VAT number"),
    GLN("0088", "GLN", "GS1"),
    GR_VAT("9933", "GR:VAT", "Greece VAT number"),
    HR_VAT("9934", "HR:VAT", "Croatia VAT number"),
    HU_VAT("9910", "HU:VAT", "Hungarian Tax Board"),
    IBAN("9918", "IBAN", "S.W.I.F.T. Society for Worldwide Interbank Financial Telecommunications s.c."),
    IE_VAT("9935", "IE:VAT", "Ireland VAT number"),
    IS_KT("9917", "IS:KT", "Icelandic National Registry"),
    ISO6523("0028", "ISO6523", "ISO (International Organization for Standardization)"),
    IT_CF("9907", "IT:CF", "TAX Authority"),
    IT_FTI("0097", "IT:FTI", "Ediforum Italia"),
    IT_IPA("9921", "IT:IPA", "Indice delle Pubbliche Amministrazioni"),
    IT_SECETI("0142", "IT:SECETI", "Servizi Centralizzati SECETI"),
    IT_SIA("0135", "IT:SIA", "Società Interbancaria per l Automazione"),
    IT_VAT("9906", "IT:VAT", "Ufficio responsabile gestione partite IVA"),
    LI_VAT("9936", "LI:VAT", "Liechtenstein VAT number"),
    LT_VAT("9937", "LT:VAT", "Lithuania VAT number"),
    LU_VAT("9938", "LU:VAT", "Luxemburg VAT number"),
    LV_VAT("9939", "LV:VAT", "Latvia VAT number"),
    MC_VAT("9940", "MC:VAT", "Monaco VAT number"),
    ME_VAT("9941", "ME:VAT", "Montenegro VAT number"),
    MK_VAT("9942", "MK:VAT", "Macedonia, the former Yugoslav Republic of VAT number"),
    MT_VAT("9943", "MT:VAT", "Malta VAT number"),
    NL_KVK("0106", "NL:KVK", "Vereniging van Kamers van Koophandel en Fabrieken in Nederland"),
    NL_OIN("9954", "NL:OIN", "Dutch OverheidsIdentificatieNummer"),
    NL_VAT("9944", "NL:VAT", "Netherlands VAT number"),
    NO_ORG("0192", "NO:ORG", "The Brønnøysund Register Centre"),
    NO_ORGNR("9908", "NO:ORGNR", "Enhetsregisteret ved Brønnøysundregistrene"),
    NO_VAT("9909", "NO:VAT", "Enhetsregisteret ved Brønnøysundregistrene"),
    PL_VAT("9945", "PL:VAT", "Poland VAT number"),
    PT_VAT("9946", "PT:VAT", "Portugal VAT number"),
    RO_VAT("9947", "RO:VAT", "Romania VAT number"),
    RS_VAT("9948", "RS:VAT", "Serbia VAT number"),
    SE_ORGNR("0007", "SE:ORGNR", "The Swedish National Tax Board"),
    SE_VAT("9955", "SE:VAT", "Swedish VAT number"),
    SG_UEN("0195", "SG:UEN", "Singapore Nationwide elnvoice Framework"),
    SI_VAT("9949", "SI:VAT", "Slovenia VAT number"),
    SK_VAT("9950", "SK:VAT", "Slovakia VAT number"),
    SM_VAT("9951", "SM:VAT", "San Marino VAT number"),
    TR_VAT("9952", "TR:VAT", "Turkey VAT number"),
    VA_VAT("9953", "VA:VAT", "Holy See (Vatican City State) VAT number");

    private final String code;
    private final String schemeId;
    private final String description;

    @Getter(lazy = true) private static final Map<String, ICD> icdMap = createIcdMap();

    private static Map<String, ICD> createIcdMap() {
        return EnumSet.allOf(ICD.class)
                .stream()
                .collect(Collectors.toMap(ICD::getCode, Function.identity()));
    }

    ICD(String code, String schemeId, String description) {
        this.code = code;
        this.schemeId = schemeId;
        this.description = description;
    }

    public String getCountryCode() {
        return schemeId.contains(":")
                ? schemeId.split(":")[0]
                : schemeId;
    }

    @Override
    public String toString() {
        return code;
    }

    public static ICD parse(String icd) {
        return Optional.ofNullable(getIcdMap().get(icd))
                .orElseThrow(() -> new IllegalArgumentException(String.format("ISO 3166-2 code not found for ISO 6523 identifier '%s'.", icd)));
    }

    public boolean isNorway() {
        return this == ICD.NO_ORGNR || this == ICD.NO_ORG;
    }
}
