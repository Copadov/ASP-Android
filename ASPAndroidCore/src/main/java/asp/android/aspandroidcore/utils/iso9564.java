package asp.android.aspandroidcore.utils;

import java.util.Arrays;

public class iso9564
{
    String logDebug1 = "";
    String logDebug2 = "";
    String logDebug3 = "";
    int index;
    int desplaz;
    int longt;
    private byte[] EncripterKey = new byte[64];
    private byte[] TraslaterKey = new byte[64];
    static byte[] xorpin = new byte[65];
    static byte[] xorpan = new byte[65];
    static byte[] bt = new byte[65];
    static byte[] bt2 = new byte[65];
    static byte[] bt3 = new byte[65];
    static byte[] pinb = new byte[17];
    private byte[] tarjb = new byte[64];
    byte pinpad;
    byte vdatapad;
    byte[] Upinpad = new byte[2];
    byte[] Uvdatapad = new byte[2];
    static byte[] out1 = new byte[65];
    static byte[] out2 = new byte[65];
    static byte[] out3 = new byte[65];
    static byte[][] k_pvk1 = new byte[16][65];
    static byte[][] k_pvk2 = new byte[16][65];
    static char[] tarjeta = new char[17];
    static byte[] tarjetas = new byte[17];
    static char[] tarj = new char[17];
    static char[] datacard = new char[17];
    byte[] tbdec = { 5, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5 };
    int iDebug = -1;
    int iNivelDebug = -1;
    private static byte[][] sources = { { 14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7, 0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8, 4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0, 15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13 }, { 15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10, 3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5, 0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15, 13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9 }, { 10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8, 13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1, 13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7, 1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12 }, { 7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15, 13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9, 10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4, 3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14 }, { 2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9, 14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6, 4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14, 11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3 }, { 12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11, 10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8, 9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6, 4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13 }, { 4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1, 13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6, 1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2, 6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12 }, { 13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7, 1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2, 7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8, 2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11 } };
    private static byte[] InitialTr = { 58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 44, 36, 28, 20, 12, 4, 62, 54, 46, 38, 30, 22, 14, 6, 64, 56, 48, 40, 32, 24, 16, 8, 57, 49, 41, 33, 25, 17, 9, 1, 59, 51, 43, 35, 27, 19, 11, 3, 61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31, 23, 15, 7 };
    private static byte[] FinalTr = { 40, 8, 48, 16, 56, 24, 64, 32, 39, 7, 47, 15, 55, 23, 63, 31, 38, 6, 46, 14, 54, 22, 62, 30, 37, 5, 45, 13, 53, 21, 61, 29, 36, 4, 44, 12, 52, 20, 60, 28, 35, 3, 43, 11, 51, 19, 59, 27, 34, 2, 42, 10, 50, 18, 58, 26, 33, 1, 41, 9, 49, 17, 57, 25 };
    private static byte[] swap = { 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32 };
    private static byte[] KeyTr1 = { 57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 42, 34, 26, 18, 10, 2, 59, 51, 43, 35, 27, 19, 11, 3, 60, 52, 44, 36, 63, 55, 47, 39, 31, 23, 15, 7, 62, 54, 46, 38, 30, 22, 14, 6, 61, 53, 45, 37, 29, 21, 13, 5, 28, 20, 12, 4 };
    private static byte[] KeyTr2 = { 14, 17, 11, 24, 1, 5, 3, 28, 15, 6, 21, 10, 23, 19, 12, 4, 26, 8, 16, 7, 27, 20, 13, 2, 41, 52, 31, 37, 47, 55, 30, 40, 51, 45, 33, 48, 44, 49, 39, 56, 34, 53, 46, 42, 50, 36, 29, 32 };
    private static byte[] etr = { 32, 1, 2, 3, 4, 5, 4, 5, 6, 7, 8, 9, 8, 9, 10, 11, 12, 13, 12, 13, 14, 15, 16, 17, 16, 17, 18, 19, 20, 21, 20, 21, 22, 23, 24, 25, 24, 25, 26, 27, 28, 29, 28, 29, 30, 31, 32, 1 };
    private static byte[] ptr = { 16, 7, 20, 21, 29, 12, 28, 17, 1, 15, 23, 26, 5, 18, 31, 10, 2, 8, 24, 14, 32, 27, 3, 9, 19, 13, 30, 6, 22, 11, 4, 25 };
    private static byte[] rots = { 1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1 };

    public iso9564()
    {
        System.err.println("Java Jar Lib DES ANSI ISO9564 by SFC Gt 2016");
    }

    public void ConvertToBin(byte[] strtxt, byte[] nume)
    {
        byte number = -1;
        for (int m = 0; m < 65; m++) {
            nume[m] = 0;
        }
        for (int i = 0; i < 16; i++)
        {
            int j = i * 4 + 3;


            char caracter = (char)strtxt[i];
            switch (caracter)
            {
                case '0':
                    number = 0;
                    break;
                case '1':
                    number = 1;
                    break;
                case '2':
                    number = 2;
                    break;
                case '3':
                    number = 3;
                    break;
                case '4':
                    number = 4;
                    break;
                case '5':
                    number = 5;
                    break;
                case '6':
                    number = 6;
                    break;
                case '7':
                    number = 7;
                    break;
                case '8':
                    number = 8;
                    break;
                case '9':
                    number = 9;
                    break;
                case 'A':
                    number = 10;
                    break;
                case 'B':
                    number = 11;
                    break;
                case 'C':
                    number = 12;
                    break;
                case 'D':
                    number = 13;
                    break;
                case 'E':
                    number = 14;
                    break;
                case 'F':
                    number = 15;
            }
            byte cociente = number;
            while ((cociente > 0) && (cociente <= 15))
            {
                nume[j] = ((byte)(cociente % 2));

                cociente = (byte)(cociente / 2);
                j--;
            }
        }
    }

    public void UnConvertToBin(byte[] nume, int n)
    {
        int posicion = 0;
        for (int m = 0; m < 17; m++) {
            tarj[m] = '\000';
        }
        for (byte i = 0; i < n;)
        {
            byte number = 0;
            for (byte j = 0; j < 4; j = (byte)(j + 1))
            {
                number = (byte)(number * 2);
                number = (byte)(number + nume[i]);
                i = (byte)(i + 1);
            }
            switch (number)
            {
                case 0:
                    tarj[posicion] = '0';

                    posicion++;
                    break;
                case 1:
                    tarj[posicion] = '1';

                    posicion++;
                    break;
                case 2:
                    tarj[posicion] = '2';
                    posicion++;
                    break;
                case 3:
                    tarj[posicion] = '3';
                    posicion++;
                    break;
                case 4:
                    tarj[posicion] = '4';
                    posicion++;
                    break;
                case 5:
                    tarj[posicion] = '5';
                    posicion++;
                    break;
                case 6:
                    tarj[posicion] = '6';
                    posicion++;
                    break;
                case 7:
                    tarj[posicion] = '7';
                    posicion++;
                    break;
                case 8:
                    tarj[posicion] = '8';
                    posicion++;
                    break;
                case 9:
                    tarj[posicion] = '9';
                    posicion++;
                    break;
                case 10:
                    tarj[posicion] = 'A';
                    posicion++;
                    break;
                case 11:
                    tarj[posicion] = 'B';
                    posicion++;
                    break;
                case 12:
                    tarj[posicion] = 'C';
                    posicion++;
                    break;
                case 13:
                    tarj[posicion] = 'D';
                    posicion++;
                    break;
                case 14:
                    tarj[posicion] = 'E';
                    posicion++;
                    break;
                case 15:
                    tarj[posicion] = 'F';
                    posicion++;
            }
        }
    }

    public void ClearLogDbg()
    {
        this.logDebug1 = "";
        this.logDebug2 = "";
        this.logDebug3 = "";
    }

    public void UnGenOr(byte[] key, byte[] cifrado, byte[] salida)
    {
        for (byte i = 0; i < 64; i = (byte)(i + 1)) {
            salida[i] = ((byte)(cifrado[i] | key[i]));
        }
    }

    public void UnGenAnd(byte[] key, byte[] cifrado, byte[] salida)
    {
        for (byte i = 0; i < 64; i = (byte)(i + 1)) {
            salida[i] = ((byte)(cifrado[i] & key[i]));
        }
    }

    public void UnGenXor(byte[] key, byte[] cifrado, byte[] salida)
    {
        for (byte i = 0; i < 64; i = (byte)(i + 1)) {
            salida[i] = ((byte)(cifrado[i] ^ key[i]));
        }
    }

    public void GenXor(byte[] bla, byte[] blb)
    {
        this.logDebug1 += "XOR BLOQUE PRIMARIO/SECUNDARIO = ";
        for (byte i = 0; i < 64; i = (byte)(i + 1))
        {
            bla[i] = ((byte)(bla[i] ^ blb[i]));
            this.logDebug1 += bla[i];
        }
        this.logDebug1 += "\n";
    }

    public void GenOr(byte[] bla, byte[] blb)
    {
        this.logDebug1 += "OR BLOQUE PRIMARIO/SECUNDARIO = ";
        for (byte i = 0; i < 64; i = (byte)(i + 1))
        {
            bla[i] = ((byte)(bla[i] | blb[i]));
            this.logDebug1 += bla[i];
        }
        this.logDebug1 += "\n";
    }

    public void GenAnd(byte[] bla, byte[] blb)
    {
        this.logDebug1 += "AND BLOQUE PRIMARIO/SECUNDARIO = ";
        for (byte i = 0; i < 64; i = (byte)(i + 1))
        {
            bla[i] = ((byte)(bla[i] & blb[i]));
            this.logDebug1 += bla[i];
        }
        this.logDebug1 += "\n";
    }

    private void Transposition(byte[] data, byte[] t, int n)
    {
        byte[] x = new byte[64];
        for (int a = 0; a < 64; a++) {
            x[a] = data[a];
        }
        for (byte l = 0; l < n; l = (byte)(l + 1)) {
            data[l] = x[(t[l] - 1)];
        }
    }

    private void TraslaterKey(byte[] key)
    {
        byte[] x = new byte[64];
        for (int a = 0; a < 64; a++) {
            x[a] = key[a];
        }
        for (byte i = 0; i < 55; i = (byte)(i + 1)) {
            key[i] = x[(i + 1)];
        }
        key[27] = x[0];
        key[55] = x[28];
    }

    public void GenerateKey(byte[] num, byte[][] k1)
    {
        byte[] tkey = new byte[64];
        byte[] keys = new byte[65];
        for (int a = 0; a < 65; a++) {
            keys[a] = 0;
        }
        ConvertToBin((byte[])num, (byte[])keys);
        for (int a = 0; a < 64; a++) {
            tkey[a] = keys[a];
        }
        Transposition(tkey, KeyTr1, 56);
        for (byte i = 0; i < 16; i = (byte)(i + 1))
        {
            for (byte j = 0; j < rots[i]; j = (byte)(j + 1)) {
                TraslaterKey(tkey);
            }
            for (int a = 0; a < 64; a++) {
                k1[i][a] = tkey[a];
            }
            Transposition(k1[i], KeyTr2, 48);
        }
    }

    private void FormatKey(byte i, byte[] a, byte[] x, byte[][] ky)
    {
        byte[] e = new byte[64];
        byte[] ikey = new byte[64];
        byte[] y = new byte[64];
        for (int w = 0; w < 64; w++) {
            e[w] = a[w];
        }
        Transposition(e, etr, 48);
        for (int z = 0; z < 64; z++) {
            ikey[z] = ky[i][z];
        }
        for (byte j = 0; j < 48; j = (byte)(j + 1)) {
            y[j] = ((byte)((e[j] + ikey[j]) % 2));
        }
        for (byte k = 1; k < 9; k = (byte)(k + 1))
        {
            byte r = (byte)(32 * y[(6 * k - 6)] + 16 * y[(6 * k - 1)] + 8 * y[(6 * k - 5)] + 4 * y[(6 * k - 4)] + 2 * y[(6 * k - 3)] + y[(6 * k - 2)]);
            x[(4 * k - 4)] = ((byte)(sources[(k - 1)][r] / 8 % 2));
            x[(4 * k - 3)] = ((byte)(sources[(k - 1)][r] / 4 % 2));
            x[(4 * k - 2)] = ((byte)(sources[(k - 1)][r] / 2 % 2));
            x[(4 * k - 1)] = ((byte)(sources[(k - 1)][r] % 2));
        }
        Transposition(x, ptr, 32);
    }

    public String UndecripTerTransport(String block1, String ll)
    {
        String asnundecrip = "";

        byte[] ansp1 = new byte[17];
        byte[] strKey = new byte[17];
        byte[] ansp2 = new byte[65];
        byte[] salida1 = new byte[65];
        for (int x = 0; x < 16; x++) {
            ansp1[x] = ((byte)block1.charAt(x));
        }
        for (int x = 0; x < 16; x++) {
            strKey[x] = ((byte)ll.charAt(x));
        }
        GenerateKey(strKey, k_pvk2);
        ConvertToBin(ansp1, salida1);
        unEncripterToDes(salida1, ansp2, k_pvk2);


        UnConvertToBin(ansp2, 64);
        for (int x = 0; x < 16; x++) {
            asnundecrip = asnundecrip + tarj[x];
        }
        //System.out.println("Valor en claro   " + asnundecrip);
        return asnundecrip;
    }

    public void EncripterToDes(byte[] plaintext, byte[] ciphertext, byte[][] k)
    {
        byte[] a = new byte[64];
        byte[] b = new byte[64];
        byte[] x = new byte[64];
        for (int t1 = 0; t1 < 64; t1++) {
            a[t1] = plaintext[t1];
        }
        Transposition(a, InitialTr, 64);
        for (byte i = 0; i < 16; i = (byte)(i + 1))
        {
            for (int t1 = 0; t1 < 64; t1++) {
                b[t1] = a[t1];
            }
            for (byte j = 0; j < 32; j = (byte)(j + 1)) {
                a[j] = b[(j + 32)];
            }
            FormatKey(i, a, x, k);
            for (int j = 0; j < 32; j = (byte)(j + 1)) {
                a[(j + 32)] = ((byte)((b[j] + x[j]) % 2));
            }
        }
        Transposition(a, swap, 64);
        Transposition(a, FinalTr, 64);
        for (int t1 = 0; t1 < 64; t1++) {
            ciphertext[t1] = a[t1];
        }
    }

    public void unEncripterToDes(byte[] plaintext, byte[] ciphertext, byte[][] k)
    {
        byte[] a = new byte[64];
        byte[] b = new byte[64];
        byte[] x = new byte[64];
        for (int t1 = 0; t1 < 64; t1++) {
            a[t1] = plaintext[t1];
        }
        Transposition(a, InitialTr, 64);
        for (int i = 15; i >= 0; i--)
        {
            for (int t1 = 0; t1 < 64; t1++) {
                b[t1] = a[t1];
            }
            for (byte j = 0; j < 32; j = (byte)(j + 1)) {
                a[j] = b[(j + 32)];
            }
            FormatKey((byte)i, (byte[])a, (byte[])x, (byte[][])k);
            for (int j = 0; j < 32; j = (byte)(j + 1)) {
                a[(j + 32)] = ((byte)((b[j] + x[j]) % 2));
            }
        }
        Transposition(a, swap, 64);
        Transposition(a, FinalTr, 64);
        for (int t1 = 0; t1 < 64; t1++) {
            ciphertext[t1] = a[t1];
        }
    }

    public String UnGenAsnPBlk(String tarjeta1, String pinblock, String wk_clear, int desp)
    {
        return UnGenAsnPBlk(tarjeta1, pinblock, wk_clear, desp, 4);
    }

    public String UnGenAsnPBlk(String tarjeta1, String pinblock, String wk_clear, int desp, int ilong)
    {
        tarjeta = tarjeta1.toCharArray();
        String pin = "";
        byte[] skey = new byte[17];
        byte[] llave = new byte[65];
        byte[] xorpin = new byte[65];
        byte[] xorpan = new byte[65];
        byte[] buf = new byte[17];
        byte[] pinb = new byte[17];
        byte[] tarjb = new byte[17];
        byte[] tarjb1 = new byte[65];
        int d = 0;
        int desplaz = desp;
        this.longt = 12;
        this.Upinpad[0] = 70;
        this.Uvdatapad[0] = 48;

        byte[] unxorpin = new byte[65];
        for (int t1 = 0; t1 < 16; t1++)
        {
            tarjb[t1] = 48;
            pinb[t1] = 48;
        }
        for (int index = 0; index < this.longt; index++) {
            tarjb[index] = ((byte)tarjeta[(index + desplaz)]);
        }
        for (this.index = 16; this.index >= 16 - this.longt + 1; this.index -= 1) {
            tarjb[(this.index - 1)] = tarjb[(this.index - (16 - this.longt) - 1)];
        }
        for (this.index = (15 - this.longt); this.index >= 0; this.index -= 1) {
            tarjb[this.index] = this.Uvdatapad[0];
        }
        ConvertToBin(tarjb, xorpan);

        ConvertToBin(pinb, xorpin);

        GenerateKey(wk_clear.getBytes(), k_pvk1);

        ConvertToBin(pinblock.getBytes(), out1);

        unEncripterToDes(out1, tarjb1, k_pvk1);

        UnConvertToBin(tarjb1, 64);
        for (int x = 0; x < 16; x++) {
            tarjetas[x] = ((byte)tarj[x]);
        }
        ConvertToBin(tarjetas, out1);
        String xorpan1 = "";
        for (d = 0; d < 16; d++) {
            xorpan1 = xorpan1 + tarjb[d];
        }
        UnGenXor(xorpan, out1, unxorpin);
        UnConvertToBin(unxorpin, 64);
        pin = "";
        for (d = 0; d < ilong; d++) {
            pin = pin + tarj[(d + 2)];
        }
        return pin;
    }

    public String GenAsnPBlk(String ncard, String npass, int dsp, int longncard)
    {
        return GenAsnPBlk(ncard, npass, dsp, longncard, 4);
    }

    public String GenAsnPBlk(String ncard, String npass, int dsp, int longncard, int ilong)
    {
        this.logDebug1 = (this.logDebug1 + "BLOQUE PRIMARIO= " + ncard + "\n");
        this.logDebug1 = (this.logDebug1 + "BLOQUE SECUNDARIO = " + npass + "\n");
        this.logDebug1 = (this.logDebug1 + "LONGITUD BLOQUE PRIMARIO = " + String.valueOf(longncard) + "\n");
        this.logDebug1 = (this.logDebug1 + "CORRIMIENTO = " + String.valueOf(dsp) + "\n");


        Arrays.fill(tarjeta, '0');
        //System.out.println("\nGenAsnPBlk Parameters:" + ncard.length() + "/" + ncard + " - " + npass.length() + "/" + npass);
        //System.out.println("\nParameters desplazamiento:" + dsp + "/" + longncard);
        for (int x = 0; x < 16; x++) {
            tarjeta[x] = ncard.charAt(x);
        }
        this.desplaz = dsp;
        this.longt = longncard;
        for (this.index = tarjeta.length; this.index < 16; this.index += 1) {
            tarjeta[this.index] = ((char)this.vdatapad);
        }
        for (this.index = 0; this.index < this.longt; this.index += 1) {
            this.tarjb[this.index] = ((byte)tarjeta[(this.index + this.desplaz)]);
        }
        for (this.index = 16; this.index >= 16 - this.longt + 1; this.index -= 1) {
            this.tarjb[(this.index - 1)] = this.tarjb[(this.index - (16 - this.longt) - 1)];
        }
        for (this.index = (15 - this.longt); this.index >= 0; this.index -= 1) {
            this.tarjb[this.index] = this.vdatapad;
        }
        pinb[0] = 48;
        if (ilong == 6) {
            pinb[1] = 54;
        } else {
            pinb[1] = 52;
        }
        for (this.index = 0; this.index < ilong; this.index += 1) {
            pinb[(this.index + 2)] = ((byte)npass.charAt(this.index));
        }
        for (this.index = (ilong + 2); this.index < 16; this.index += 1) {
            pinb[this.index] = this.pinpad;
        }
        String var1 = "";
        String var2 = "";
        for (int z = 0; z < 16; z++)
        {
            var1 = var1 + (char)this.tarjb[z];
            var2 = var2 + (char)pinb[z];
        }
        this.logDebug1 = (this.logDebug1 + "CIFRADO BLOQUE PRIMARIO= " + var1 + "\n");
        this.logDebug1 = (this.logDebug1 + "CIFRADO BLOQUE SECUNDARIO = " + var2 + "\n");

        ConvertToBin(this.tarjb, xorpan);
        ConvertToBin(pinb, xorpin);
        GenXor(xorpan, xorpin);
        UnConvertToBin(xorpan, 64);
        String pb = "";
        for (int x = 0; x < 16; x++) {
            pb = pb + tarj[x];
        }
        this.logDebug1 = (this.logDebug1 + "TEXTO BLOQUE SECUNDARIO XOR BLOQUE SECUNDARIO=" + pb + "\n");

        GenerateKey(this.EncripterKey, k_pvk1);
        EncripterToDes(xorpan, out1, k_pvk1);
        UnConvertToBin(out1, 64);

        var1 = "";
        for (int x = 0; x < 16; x++) {
            var1 = var1 + tarj[x];
        }
        this.logDebug1 = (this.logDebug1 + "CIFRADO BLOQUE PRIMARIO CON LAVE PRIMARIO = " + var1 + "\n");

        unEncripterToDes(out1, this.tarjb, k_pvk1);
        UnConvertToBin(this.tarjb, 64);
        var1 = "";
        for (int x = 0; x < 16; x++) {
            var1 = var1 + tarj[x];
        }
        this.logDebug1 = (this.logDebug1 + "DES CIFRADO BLOQEU PRIMARIO CON LLAVE PRIMARIA= " + var1 + "\n");

        GenerateKey(this.TraslaterKey, k_pvk1);
        EncripterToDes(this.tarjb, out1, k_pvk1);
        UnConvertToBin(out1, 64);

        pb = "";
        for (int x = 0; x < 16; x++) {
            pb = pb + tarj[x];
        }
        this.logDebug1 = (this.logDebug1 + "CIFRADO APB CON LLAVE SECUNDARIA=" + pb + "\n");
        return pb;
    }

    public String getPvv(String tsp, String pvkA, String pvkB)
    {
        int cant = 0;
        GenerateKey(pvkA.getBytes(), k_pvk1);
        ConvertToBin(tsp.getBytes(), bt);
        EncripterToDes(bt, out1, k_pvk1);
        UnConvertToBin(out1, 64);
        for (int x = 0; x < 17; x++) {
            tarjetas[x] = ((byte)tarj[x]);
        }
        GenerateKey(pvkB.getBytes(), k_pvk2);
        ConvertToBin(tarjetas, bt2);

        unEncripterToDes(bt2, out2, k_pvk2);
        UnConvertToBin(out2, 64);
        for (int x = 0; x < 17; x++) {
            tarjetas[x] = ((byte)tarj[x]);
        }
        ConvertToBin(tarjetas, bt3);
        EncripterToDes(bt3, out3, k_pvk1);
        UnConvertToBin(out3, 64);
        for (int x = 0; x < 17; x++) {
            tarjetas[x] = ((byte)tarj[x]);
        }
        String pvv = "";
        for (int x = 0; x < 16; x++)
        {
            char xx = (char)tarjetas[x];
            if ((xx >= '0') && (xx <= '9') && (cant <= 3))
            {
                pvv = pvv + String.valueOf(xx);

                cant++;
            }
        }
        if (cant < 3) {
            for (int x = 0; x < 4; x++)
            {
                char xx = (char)tarjetas[x];
                if ((xx >= '=') && (xx <= 'F') && (cant <= 3))
                {
                    if ((tarjetas[x] == 65) || (tarjetas[x] == 97)) {
                        pvv = pvv + 0;
                    }
                    if ((tarjetas[x] == 66) || (tarjetas[x] == 98)) {
                        pvv = pvv + 1;
                    }
                    if ((tarjetas[x] == 67) || (tarjetas[x] == 99)) {
                        pvv = pvv + 2;
                    }
                    if ((tarjetas[x] == 68) || (tarjetas[x] == 100)) {
                        pvv = pvv + 3;
                    }
                    if ((tarjetas[x] == 69) || (tarjetas[x] == 101)) {
                        pvv = pvv + 4;
                    }
                    if ((tarjetas[x] == 70) || (tarjetas[x] == 102)) {
                        pvv = pvv + 5;
                    }
                    cant++;
                }
            }
        }
        return pvv;
    }

    public void setKeyEncripterTralaterKey(String keyA, String keyB)
    {
        this.logDebug1 = (this.logDebug1 + "LLAVE PIN = " + keyA + "\n");
        this.logDebug1 = (this.logDebug1 + "LLAVE COMMS = " + keyB + "\n");
        for (int x = 0; x < 16; x++)
        {
            this.EncripterKey[x] = ((byte)keyA.charAt(x));
            this.TraslaterKey[x] = ((byte)keyB.charAt(x));
        }
    }

    public void setDebugEnable(int iEnable, int iLevel)
    {
        this.iDebug = iEnable;
        this.iNivelDebug = iLevel;
    }

    public void setPinPadChar(char cPPC)
    {
        this.pinpad = ((byte)cPPC);
        this.logDebug1 = (this.logDebug1 + "CARACTER DE RELLENO BLOQUE SECUNDARIO = " + cPPC + "\n");
    }

    public void setPanPadChar(char cPPC)
    {
        this.vdatapad = ((byte)cPPC);
        this.logDebug1 = (this.logDebug1 + "CARACTER DE RELLENO BLOQUE PRIMARIO = " + cPPC + "\n");
    }

    public String getLog9564()
    {
        if (this.iDebug == 1) {
            return this.logDebug1;
        }
        return "";
    }
}