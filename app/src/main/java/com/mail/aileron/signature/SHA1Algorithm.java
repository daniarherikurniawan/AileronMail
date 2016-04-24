package com.mail.aileron.signature;
import java.math.*;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author v8areu
 */
public class SHA1Algorithm {


    private BigInteger bufferA = new BigInteger("67452301", 16);                 //declare bigint in hex
    private BigInteger bufferB = new BigInteger("EFCDAB89", 16);
    private BigInteger bufferC = new BigInteger("98BADCFE", 16);
    private BigInteger bufferD = new BigInteger("10325476", 16);
    private BigInteger bufferE = new BigInteger("C3D2E1F0", 16);
    private BigInteger[] roundConstant = {
        new BigInteger("5A827999", 16),
        new BigInteger("6ED9EBA1", 16),
        new BigInteger("8F1BBCDC", 16),
        new BigInteger("CA62C1D6", 16),
    };
    private String message;
    private BigInteger messageLength ;
    private BigInteger multipleOf;

    public SHA1Algorithm(String message) {
        this.message = message;
        messageLength = new BigInteger(message.length() + "");
        multipleOf = BigInteger.ONE.add(BigInteger.valueOf(64).add(messageLength.multiply(BigInteger.valueOf(8))).divide(BigInteger.valueOf(512)));
    }

    public String toBigInt(String message) {
        BigInteger tempMessageLength = messageLength;
        String temp = String.format("%x", new BigInteger(1, message.getBytes()));           //string to hex        String temp = String.format("%x", new BigInteger(1, message.getBytes()));           //string to hex
        String outputMessage = new BigInteger("1" + temp, 16).toString(2).substring(1);     //hex to binary
        BigInteger paddingLength = new BigInteger("512").multiply(multipleOf).subtract(messageLength.multiply(new BigInteger("8"))).subtract(new BigInteger("64"));
        outputMessage = outputMessage + "1";
        for (BigInteger bi = BigInteger.valueOf(1);
                bi.compareTo(paddingLength) < 0;
                bi = bi.add(BigInteger.ONE)) {
            outputMessage = outputMessage + "0";
        }
        for (BigInteger bi = BigInteger.valueOf(60);
                bi.compareTo(BigInteger.ZERO) >= 0;
                bi = bi.subtract(BigInteger.ONE)) {

            if (tempMessageLength.compareTo(new BigInteger("2").pow(bi.intValue())) >= 0) {
                outputMessage = outputMessage + "1";
                tempMessageLength = tempMessageLength.subtract(new BigInteger("2").pow(bi.intValue()));

            }
            else {
                outputMessage = outputMessage + "0";
            }
        }
        outputMessage = outputMessage + "000";
        return outputMessage;                                                //return in string binary
    }
    public String[] toBlockMessage(String outputMessage) {
        int outputMessageLength = outputMessage.length();
        int numBlockMessage = outputMessageLength/512;
        //System.out.println(outputMessageLength + " " + numBlockMessage);
        String[] blockMessage = new String[numBlockMessage];

        //------------- split the message into n-blocks --------------
        //System.out.println(outputMessageLength + " " + numBlockMessage);
        for (int i=0; i < numBlockMessage; i++) {
            blockMessage[i] = outputMessage.substring(0+i*512, 512+i*512);
            //System.out.println(blockMessage[i]);
        }
        return blockMessage;
    }
    public String functionSHA(String[] blockMessage) {
        BigInteger bigMessageDigest;
        String A, B, C, D, E;

        BigInteger bigA;  BigInteger tempBigA; BigInteger tempBigA2;
        BigInteger bigB;
        BigInteger bigC;  BigInteger tempBigC;
        BigInteger bigD;
        BigInteger bigE;

        BigInteger hashA = bufferA;
        BigInteger hashB = bufferB;
        BigInteger hashC = bufferC;
        BigInteger hashD = bufferD;
        BigInteger hashE = bufferE;

        String newA;
        String newB;
        String newC;
        String newD;
        String newE;

        BigInteger functionF = BigInteger.ZERO;
        BigInteger tempRoundConstant = BigInteger.ZERO;

        String[] wordBlock = new String[80];
        String[] wordBlockTemp = new String[64];

        BigInteger[] wordBigTemp = new BigInteger[64];
        BigInteger[] wordBig = new BigInteger[80];
        //------------ complex looping process --------------
        for (int i=0; i<multipleOf.intValue(); i++) {
            //-------------- split blockmessage to 16 blocks -----------
            //-------- 0 until 15 -------
            //System.out.println("sip1");
            for (int blockCount=0; blockCount<16; blockCount++) {

                BigInteger wordTemp = BigInteger.ZERO;
                wordBig[blockCount] = BigInteger.ZERO;
                for (int innerBlock=0; innerBlock<4; innerBlock++) {
                    wordBlockTemp[blockCount*4 + innerBlock] = blockMessage[i].substring(0+
                            (blockCount*4 + innerBlock)*8,
                            8+(blockCount*4 + innerBlock)*8); //split the block message
                    wordBigTemp[blockCount*4 + innerBlock] = new BigInteger(wordBlockTemp[blockCount*4 + innerBlock], 2); //string to bigint
                    wordTemp = (wordBigTemp[blockCount*4 + innerBlock].and(new BigInteger("000000FF", 16))).shiftLeft(24 - innerBlock * 8);
                    wordBig[blockCount] = wordBig[blockCount].or(wordTemp);
                }
                wordBlock[blockCount] = wordBig[blockCount].toString(2);
                //System.out.println(blockCount + ": " +wordBlock[blockCount]);

            }
            //-------- 16 until 79 -------
            for (int blockCount=16; blockCount<80; blockCount++) {
                wordBig[blockCount] = wordBig[blockCount-3].xor(wordBig[blockCount-8]).xor(wordBig[blockCount-14]).xor(wordBig[blockCount-16]);
                wordBig[blockCount] = ((wordBig[blockCount].shiftLeft(1)).or(wordBig[blockCount].shiftRight(32-1))).and(new BigInteger("FFFFFFFF", 16));  //rotate 5 left

                wordBlock[blockCount] = new BigInteger("" + wordBig[blockCount]).toString(2);                       //bigint to binary
                //while (wordBlock[blockCount].length() < 32){                                         //<< for leading zeros
                //    wordBlock[blockCount] = "0"+ wordBlock[blockCount];
                //}
                //System.out.println(blockCount + ": " +wordBlock[blockCount]);
            }

            //System.out.println("try");
                bigA = hashA;
                bigB = hashB;
                bigC = hashC;
                bigD = hashD;
                bigE = hashE;

                A = new BigInteger("" + hashA).toString(2);                       //bigint to binary
                B = new BigInteger("" + hashB).toString(2);                       //bigint to binary
                C = new BigInteger("" + hashC).toString(2);                       //bigint to binary
                D = new BigInteger("" + hashD).toString(2);                       //bigint to binary
                E = new BigInteger("" + hashE).toString(2);                       //bigint to binary


            for (int cycle = 0; cycle < 80; cycle++) {
                //System.out.println(cycle + ": " + bigB.toString(2));
                //-------------- hitung fungsi f -------------
                if (cycle >= 0 && cycle <= 19) {
                    tempRoundConstant = roundConstant[0];
                    functionF = (bigB.and(bigC)).or((bigB.not()).and(bigD));
                    //System.out.println(bigB.toString(2) + " \n" + bigC.toString(2) + " " + bigD.toString(2) + " \n" + functionF.toString(2));
                    //System.out.println(cycle + ": " + (bigB.not()).toString(2));
                }

                else if (cycle >= 20 && cycle <= 39) {
                    tempRoundConstant = roundConstant[1];
                    functionF = bigB.xor(bigC).xor(bigD);

                }

                else if (cycle >= 40 && cycle <= 59) {
                    tempRoundConstant = roundConstant[2];
                    functionF = (bigB.and(bigC)).or((bigB.and(bigD))).or((bigC.and(bigD)));

                }

                else if (cycle >= 60 && cycle <= 79) {
                    tempRoundConstant = roundConstant[3];
                    functionF = bigB.xor(bigC).xor(bigD);
                }

                //---------- for A ----------
                tempBigA2 = ((bigA.shiftLeft(5)).or(bigA.shiftRight(32-5))).and(new BigInteger("FFFFFFFF", 16));  //rotate 5 left
                tempBigA = (bigE.add(functionF).add(tempBigA2).add(wordBig[cycle]).add(tempRoundConstant)).mod((BigInteger.valueOf(2).pow(32)));
                newA = tempBigA.toString(2);  //to binary
                //System.out.println(cycle + ": " + newE);
                //---------- for B ----------
                newB = A;

                //---------- for C ----------
                tempBigC = ((bigB.shiftLeft(30)).or(bigB.shiftRight(32-30))).and(new BigInteger("FFFFFFFF", 16));  //rotate 30 left
                newC = tempBigC.toString(2);  //to binary

                //---------- for D ----------
                newD = C;

                //---------- for E ----------
                newE = D;

                //------------ re-initialization -------------
                A = newA;
                B = newB;
                C = newC;
                D = newD;
                E = newE;

                bigA = new BigInteger(newA, 2);
                bigB = new BigInteger(newB, 2);
                bigC = new BigInteger(newC, 2);
                bigD = new BigInteger(newD, 2);
                bigE = new BigInteger(newE, 2);
            }
            hashA = hashA.add(bigA).mod((BigInteger.valueOf(2).pow(32)));
            hashB = hashB.add(bigB).mod((BigInteger.valueOf(2).pow(32)));
            hashC = hashC.add(bigC).mod((BigInteger.valueOf(2).pow(32)));
            hashD = hashD.add(bigD).mod((BigInteger.valueOf(2).pow(32)));
            hashE = hashE.add(bigE).mod((BigInteger.valueOf(2).pow(32)));
        }
        bigMessageDigest = hashA.shiftLeft(128).or(hashB.shiftLeft(96)).or(hashC.shiftLeft(64)).or(hashD.shiftLeft(32)).or(hashE);
        return bigMessageDigest.toString(16);
    }
    public String computeHash(String message) {
        String outputBig = toBigInt(message);
        String[] blockMessage = toBlockMessage(outputBig);
        String outputMessageDigest = functionSHA(blockMessage);
        while (outputMessageDigest.length() <40) {
            outputMessageDigest = "0" + outputMessageDigest;
        }
        return outputMessageDigest;
    }

}
