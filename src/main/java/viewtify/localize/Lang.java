/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.localize;

import kiss.Signal;
import kiss.Variable;

public enum Lang {

    AA, AB, AE, AF, AK, AM, AN, AR, AS, AV, AY, AZ, BA, BE, BG, BH, BI, BM, BN, BO, BR, BS, CA, CE, CH, CO, CR, CS, CU, CV, CY, DA, DE, DV, DZ, EE, EL, EN, EO, ES, ET, EU, FA, FF, FI, FJ, FO, FR, FY, GA, GD, GL, GN, GU, GV, HA, HE, HI, HO, HR, HT, HU, HY, HZ, IA, ID, IE, IG, II, IK, IN, IO, IS, IT, IU, IW, JA, JI, JV, KA, KG, KI, KJ, KK, KL, KM, KN, KO, KR, KS, KU, KV, KW, KY, LA, LB, LG, LI, LN, LO, LT, LU, LV, MG, MH, MI, MK, ML, MN, MO, MR, MS, MT, MY, NA, NB, ND, NE, NG, NL, NN, NO, NR, NV, NY, OC, OJ, OM, OR, OS, PA, PI, PL, PS, PT, QU, RM, RN, RO, RU, RW, SA, SC, SD, SE, SG, SI, SK, SL, SM, SN, SO, SQ, SR, SS, ST, SU, SV, SW, TA, TE, TG, TH, TI, TK, TL, TN, TO, TR, TS, TT, TW, TY, UG, UK, UR, UZ, VE, VI, VO, WA, WO, XH, YI, YO, ZA, ZH, ZU;

    /** The default language in the current environment. */
    private static Variable<Lang> current = Variable.of(JA);

    /**
     * Retrieve the language in the current environment.
     * 
     * @return A default language.
     */
    public static Lang current() {
        return current.v;
    }

    /**
     * Observe default language modification.
     * 
     * @return
     */
    public static Signal<Lang> observe() {
        return current.observeNow();
    }

    /**
     * Change the default language in the current environment.
     * 
     * @param lang A language to set.
     */
    public static void set(Lang lang) {
        if (lang != null) {
            current.set(lang);
        }
    }

    public static void change() {
        if (current.v == EN) {
            set(JA);
        } else {
            set(EN);
        }
    }

    // Code Generator
    // public static void main(String[] args) {
    // StringJoiner joint = new StringJoiner(", ", "", ";");
    // I.signal(Locale.getISOLanguages()).sort(Comparator.naturalOrder()).to(lang -> {
    // joint.add(lang.toUpperCase());
    // });
    // System.out.println(joint);
    // }
}
