package compilation.bpp;

import static compilation.bpp.IrCompiler.*;

public class Builtin {
    public static String preInclude() {
        String rdef =     "rdef COMPR, r"+COMPR+";"
                + "rdef COMPC1, r"+COMPC1+";"
                + "rdef COMPC2, r"+COMPC2+";"
                + "rdef COMP1, r"+COMP1+";"
                + "rdef COMP2, r"+COMP2+";"
                + "rdef B1, r"+B1+";"
                + "rdef B2, r"+B2+";"
                + "rdef BC, r"+BC+";"
                + "rdef BR, r"+BR+";"
                + "rdef BOOL_IN, r"+BOOL_IN+";"
                + "rdef BOOL_OUT, r"+BOOL_OUT+";"
                + "rdef BIT_IN1, r"+BIT_IN1+";"
                + "rdef BIT_IN2, r"+BIT_IN2+";"
                + "rdef BIT_TEMP1, r"+BIT_TEMP1+";"
                + "rdef BIT_TEMP2, r"+BIT_TEMP2+";"
                + "rdef BIT_2, r"+BIT_2+";"
                + "rdef BIT_TEMPB, r"+BIT_TEMPB+";"
                + "rdef BIT_TEMPC, r"+BIT_TEMPC+";"
                + "rdef BIT_TEMPD, r"+BIT_TEMPD+";"
                + "rdef BIT_OUT, r"+BIT_OUT+";"
                + "rdef BIT_LOOPC, r"+BIT_LOOPC+";"
                + "rdef IMPORTANCE, r"+IMPORTANCE+";";
        String gt = """
                cdef gt, {
                    clear COMPR;
                    clear COMPC1;
                    clear COMPC2;
                    while COMP1 {
                        inc COMPC1, 1;
                        while COMP2 {
                            dec COMP2, 1;
                            clear COMPC1;
                            inc COMPC2, 1;
                        };
                        while COMPC1 {
                            dec COMPC1, 1;
                            inc COMPR, 1;
                        };
                        while COMPC2 {
                            dec COMPC2, 1;
                            inc COMP2, 1;
                        };
                        dec COMP2, 1;
                        dec COMP1, 1;
                    };
                };""";
        String bool = """
                cdef bool, {
                    clear BOOL_OUT;
                    while BOOL_IN {
                        inc BOOL_OUT, 1;
                        clear BOOL_IN;
                    };
                };
                cdef or, {
                    add B1, B2, BOOL_IN;
                    bool;
                    copy BOOL_OUT, BR;
                };
                cdef xor, {
                    sub B1, B2, BOOL_IN;
                    bool;
                    copy BOOL_OUT, BR;
                };""";
        String bit = """
                
                cdef bitand, {
                    clear BIT_OUT;
                    put 1, IMPORTANCE;
                    clear BIT_LOOPC;
                    put 2, BIT_2;
                    inc BIT_LOOPC, 8;
                    while BIT_LOOPC {
                        dec BIT_LOOPC, 1;
                        clear BIT_TEMPC;
                        clear BIT_TEMPD;
                        divmod BIT_IN1, IMPORTANCE, BIT_TEMP1, BIT_TEMP2;
                        divmod BIT_TEMP1, BIT_2, BIT_TEMP2, BIT_TEMPC; @ BTC = BI1 / IMP % 2
                        clear BIT_TEMP2;
                        clear BIT_TEMP1;
                        divmod BIT_IN2, IMPORTANCE, BIT_TEMP1, BIT_TEMP2;
                        divmod BIT_TEMP1, BIT_2, BIT_TEMP2, BIT_TEMPD; @ BTD = BI2 / IMP % 2
                        clear BIT_TEMP2;
                        clear BIT_TEMP1;
                        mul BIT_TEMPC, BIT_TEMPD, BIT_TEMPB;
                        mul IMPORTANCE, BIT_TEMPB, BIT_TEMP1; @ TM1 = BTC * IMP
                        mul IMPORTANCE, BIT_2, IMPORTANCE; @ IMP = IMP*2
                        add BIT_TEMP1, BIT_OUT, BIT_OUT; @ BTO = BTO + TM1
                        clear BIT_TEMP1;
                        clear BIT_TEMPB;
                    };
                };
                cdef bitor, {
                    clear BIT_OUT;
                    put 1, IMPORTANCE;
                    clear BIT_LOOPC;
                    put 2, BIT_2;
                    inc BIT_LOOPC, 8;
                    while BIT_LOOPC {
                        dec BIT_LOOPC, 1;
                        clear BIT_TEMPC;
                        clear BIT_TEMPD;
                        divmod BIT_IN1, IMPORTANCE, BIT_TEMP1, BIT_TEMP2;
                        divmod BIT_TEMP1, BIT_2, BIT_TEMP2, BIT_TEMPC; @ BTC = BI1 / IMP % 2
                        clear BIT_TEMP2;
                        clear BIT_TEMP1;
                        divmod BIT_IN2, IMPORTANCE, BIT_TEMP1, BIT_TEMP2;
                        divmod BIT_TEMP1, BIT_2, BIT_TEMP2, BIT_TEMPD; @ BTD = BI2 / IMP % 2
                        clear BIT_TEMP2;
                        clear BIT_TEMP1;
                        add BIT_TEMPC, BIT_TEMPD, BOOL_IN;
                        bool;
                        copy BOOL_OUT, BIT_TEMPB;
                        mul IMPORTANCE, BIT_TEMPB, BIT_TEMP1; @ TM1 = BTC * IMP
                        mul IMPORTANCE, BIT_2, IMPORTANCE; @ IMP = IMP*2
                        add BIT_TEMP1, BIT_OUT, BIT_OUT; @ BTO = BTO + TM1
                        clear BIT_TEMP1;
                        clear BIT_TEMPB;
                    };
                };
                cdef bitxor, {
                    clear BIT_OUT;
                    put 1, IMPORTANCE;
                    clear BIT_LOOPC;
                    put 2, BIT_2;
                    inc BIT_LOOPC, 8;
                    while BIT_LOOPC {
                        dec BIT_LOOPC, 1;
                        clear BIT_TEMPC;
                        clear BIT_TEMPD;
                        divmod BIT_IN1, IMPORTANCE, BIT_TEMP1, BIT_TEMP2;
                        divmod BIT_TEMP1, BIT_2, BIT_TEMP2, BIT_TEMPC; @ BTC = BI1 / IMP % 2
                        clear BIT_TEMP2;
                        clear BIT_TEMP1;
                        divmod BIT_IN2, IMPORTANCE, BIT_TEMP1, BIT_TEMP2;
                        divmod BIT_TEMP1, BIT_2, BIT_TEMP2, BIT_TEMPD; @ BTD = BI2 / IMP % 2
                        clear BIT_TEMP2;
                        clear BIT_TEMP1;
                        sub BIT_TEMPC, BIT_TEMPD, BOOL_IN;
                        bool;
                        copy BOOL_OUT, BIT_TEMPB;
                        mul IMPORTANCE, BIT_TEMPB, BIT_TEMP1; @ TM1 = BTC * IMP
                        mul IMPORTANCE, BIT_2, IMPORTANCE; @ IMP = IMP*2
                        add BIT_TEMP1, BIT_OUT, BIT_OUT; @ BTO = BTO + TM1
                        clear BIT_TEMP1;
                        clear BIT_TEMPB;
                    };
                };""";
        return rdef+gt+bool+bit;
    }
}
