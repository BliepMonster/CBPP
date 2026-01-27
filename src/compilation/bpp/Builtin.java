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
                + "rdef BOOL_OUT, r"+BOOL_OUT+";";
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
                cdef and, {
                    clear BR;
                    mul B1, B2, BR;
                };
                cdef bool, {
                    clear BOOL_OUT;
                    while BOOL_IN {
                        inc BOOL_OUT, 1;
                        clear BOOL_IN;
                    };
                };
                cdef or, {
                    clear BR;
                    add B1, B2, BOOL_IN;
                    bool;
                    copy BOOL_OUT, BR;
                };
                cdef xor, {
                    clear BR;
                    sub B1, B2, BOOL_IN;
                    bool;
                    copy BOOL_OUT, BR;
                };""";
        return rdef+gt+bool;
    }
}
