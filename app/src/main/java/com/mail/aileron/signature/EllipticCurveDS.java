/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ellipticcurveds;

import java.math.*;
import java.util.Random;

/**
 *
 * @author v8areu
 */

public class EllipticCurveDS {
        static final String message = "asdksjlekl13m2l1m32l1melm21kdm12kmeklm21eklm21dm1dm1dm12kldm1lkdmlkm21d2m12m12m1l";

        //----------- curve parameters ------------
        static BigInteger primeNumber = new BigInteger("6271");
        static BigInteger a = new BigInteger("2333");
        static BigInteger b = new BigInteger("1111");
	static BigInteger n = new BigInteger("6271");
        static Point basis = new Point(new BigInteger("6"), new BigInteger("491"));
        //----------- integer for maximum random number of private key generation -------------
        static BigInteger nPrivate = new BigInteger("6271");

    public static BigInteger generatePrivateKey() {
	Random rand = new Random();
	BigInteger result = new BigInteger(nPrivate.bitLength(), rand);

	while( result.compareTo(BigInteger.valueOf(1000)) <= 0) {
            result = new BigInteger(nPrivate.bitLength(), rand);
	}

	while( result.compareTo(nPrivate) >= 0) {
            result = new BigInteger(nPrivate.bitLength(), rand);
	}
	return result;
    }

    public Point generatePublicKey(BigInteger priKey) {
	BigInteger tempLambda, privateKey = priKey , xr, yr, tempX, tempY;
	xr = null;
	yr = null;
	tempX = basis.getX() ;
	tempY = basis.getY();
	tempLambda = lambdaDuplication(tempX, tempY);

	BigInteger i;
            System.out.println("Processing the public key, please wait . . .");
            for (i = new BigInteger("1"); i.compareTo(privateKey) < 0;  i = i.add(BigInteger.ONE)) {
                xr = ((( (tempLambda.pow(2)).subtract(basis.getX()).subtract(tempX)).mod(primeNumber).add(primeNumber)).mod(primeNumber));
                yr = (((tempLambda.multiply(tempX.subtract(xr))).subtract(tempY)).mod(primeNumber).add(primeNumber)).mod(primeNumber); // (a % b + b) % b modulo for giving positive value (a%b give negative!)
                tempX = xr;
                tempY = yr;
                tempLambda = lambdaAddition(basis.getX(), basis.getY(), tempX, tempY);
            }
            System.out.println("Public key is generated successfully!");
	Point PB = new Point(xr,yr);
	return PB;
    }

    public static BigInteger functionECC(BigInteger x) {
	BigInteger ySquare;
	BigInteger y = new BigInteger("1");
	ySquare = (a.multiply(x).add(x.pow(3)).add(new BigInteger(b+""))).mod(primeNumber) ;//; // this is the E function y^2 = x^3 + ax + b
        while ((y.pow(2).subtract(ySquare).mod(primeNumber)).compareTo(new BigInteger("0")) !=0 )  {
            y=y.add(new BigInteger("1"));
            if (y.compareTo(new BigInteger("1000")) == 0) {
		y = new BigInteger("0");
                    break;
            }
	}
    return y;
    }

    public static BigInteger lambdaDuplication(BigInteger xp, BigInteger yp) {
	BigInteger nominator = a.add(new BigInteger( "" + 3*(int)Math.pow(xp.doubleValue(),2)));
	BigInteger bigDenominator = yp.multiply(new BigInteger("2"));
	BigInteger inverseDenominator = bigDenominator.modInverse(primeNumber);
	BigInteger result = nominator.multiply(inverseDenominator).mod(primeNumber);
	return result;
    }

    public static BigInteger lambdaAddition(BigInteger xp, BigInteger yp, BigInteger xq, BigInteger yq) {
	BigInteger nominator = (yp.subtract(yq));
	BigInteger bigDenominator = (xp.subtract(xq));
	BigInteger inverseDenominator = bigDenominator.modInverse(primeNumber);
	BigInteger result = (nominator.multiply(inverseDenominator)).mod(primeNumber);
	return result;
    }

    //----------- signature is generated by this method --------------
    public Point signatureGeneration(String hashInput, BigInteger priKey) {
        BigInteger tempLambda, k, xr, yr, tempX, tempY, leftSignature, rightSignature, tempRight, hashBig;
        leftSignature = BigInteger.ZERO;
        rightSignature = BigInteger.ZERO;
        k = null;
        hashBig = null;
        Random rand = new Random();

        while (rightSignature.compareTo(BigInteger.ZERO) == 0) {
            System.out.println("compute right signature . . .");

            while (leftSignature.compareTo(BigInteger.ZERO) == 0) {
                System.out.println("compute left signature . . .");

                //----------- k is PRNG-ed number for computing left signature ------------
                k = new BigInteger(nPrivate.bitLength(), rand);
                while( k.compareTo(nPrivate) >= 0 ) {
                    k = new BigInteger(nPrivate.bitLength(), rand);
                }

                hashBig = new BigInteger(hashInput, 16);
                xr = BigInteger.ZERO;
                yr = BigInteger.ZERO;
                tempX = basis.getX() ;
                tempY = basis.getY();
                tempLambda = lambdaDuplication(tempX, tempY);
                    System.out.println("Processing Point KP ...");
                    for (BigInteger i = new BigInteger("1"); i.compareTo(k) < 0;  i = i.add(BigInteger.ONE)) {
                        xr = ((( (tempLambda.pow(2)).subtract(basis.getX()).subtract(tempX)).mod(primeNumber).add(primeNumber)).mod(primeNumber));
                        yr = (((tempLambda.multiply(tempX.subtract(xr))).subtract(tempY)).mod(primeNumber).add(primeNumber)).mod(primeNumber); // (a % b + b) % b modulo for giving positive value (a%b give negative!)
                        tempX = xr;
                        tempY = yr;
                        tempLambda = lambdaAddition(basis.getX(), basis.getY(), tempX, tempY);
                    }
                Point KP = new Point(xr,yr);
                leftSignature = KP.getX().mod(n);
            }

            //------------ exception handling if something is not invertible of another thing, :v -----------
            try {
                tempRight = k.modInverse(n);
                rightSignature = (tempRight.multiply(hashBig.add(priKey.multiply(leftSignature)))).mod(n);
            } catch (ArithmeticException e) {
                System.out.println("The k of (" + k + ") is not invertible to n of (" + n + ")");
                rightSignature = BigInteger.ZERO;
                leftSignature = BigInteger.ZERO;
                k = new BigInteger(nPrivate.bitLength(), rand);
                System.out.println("nilai k: " + k);
                while( k.compareTo(nPrivate) >= 0 ) {
                    k = new BigInteger(nPrivate.bitLength(), rand);
                    System.out.println("nilai k: " + k);

                }
            }
        }
        System.out.println("nilai k yang oke: " + k);
        return new Point(leftSignature, rightSignature);
    }

    public Point validSignatureGeneration(String hashInput, BigInteger priKey, Point pubKey) {
        //----------- validation of generation message to be able to be validated, not really used anymore --------------
        Point signature;
        String verification = null;
        while (true) {
            signature = signatureGeneration(hashInput, priKey);
            System.out.println("hash in main: " + hashInput);
            try {
                verification = signatureVerification(message, signature, pubKey);
                if(verification.compareTo("FIX VERIFIED")==0){
                    break;
                }

            } catch (ArithmeticException e) {
                System.out.println("right signature is not invertible");
            }
        }
        return signature;
    }

    public String signatureVerification(String message, Point signature, Point pubKey) {
        BigInteger leftSignature = signature.getX();
        BigInteger rightSignature = signature.getY();
        String result;

        //------------ verify left and right signature is on [0,n-1] ------------------
        for (BigInteger bi = BigInteger.ZERO;
                bi.compareTo(n) <0;
                bi = bi.add(BigInteger.ONE)) {

            if (bi.equals(leftSignature)) {
                System.out.println("Left signature is in between 0 until n-1");
                break;
            }

            if  (bi.equals(n.subtract(BigInteger.ONE))) {
                result = "Left Signature is NOT VERIFIED";
                return result;
            }
        }

        for (BigInteger bi = BigInteger.ZERO;
                bi.compareTo(n) <0;
                bi = bi.add(BigInteger.ONE)) {

            if (bi.equals(rightSignature)) {
                System.out.println("Right signature is in between 0 until n-1");
                break;
            }

            if  (bi.equals(n.subtract(BigInteger.ONE))) {
                result = "Right Signature is NOT VERIFIED";
                return result;
            }
        }

        SHA1Algorithm sha1Hash = new SHA1Algorithm();
        String hash = sha1Hash.computeHash(message);
        BigInteger hashBig = new BigInteger(hash, 16);
        try {
            BigInteger w = rightSignature.modInverse(n);
        } catch (ArithmeticException e) {
            System.out.println("The rightsignature of + "+ rightSignature + " is not invertible to n of (" + n + ")");
        }

        BigInteger w = rightSignature.modInverse(n);
        BigInteger u1 = (hashBig.multiply(w)).mod(n);
        BigInteger u2 = (leftSignature.multiply(w)).mod(n);

        BigInteger tempX, tempY, tempLambda, xr = null, yr = null;

        //--------------- compute u1 x basis ---------------
        tempX = basis.getX() ;
	tempY = basis.getY();
	tempLambda = lambdaDuplication(tempX, tempY);
            for (BigInteger i = new BigInteger("1"); i.compareTo(u1) < 0;  i = i.add(BigInteger.ONE)) {
                xr = ((((tempLambda.pow(2)).subtract(basis.getX()).subtract(tempX)).mod(primeNumber).add(primeNumber)).mod(primeNumber));
                yr = (((tempLambda.multiply(tempX.subtract(xr))).subtract(tempY)).mod(primeNumber).add(primeNumber)).mod(primeNumber); // (a % b + b) % b modulo for giving positive value (a%b give negative!)
                tempX = xr;
                tempY = yr;
                tempLambda = lambdaAddition(basis.getX(), basis.getY(), tempX, tempY);
            }
	Point u1P = new Point(xr,yr);

        //---------- compute u2 x pubKey ----------
        tempX = pubKey.getX() ;
	tempY = pubKey.getY();
	tempLambda = lambdaDuplication(tempX, tempY);
        for (BigInteger i = new BigInteger("1"); i.compareTo(u2) < 0;  i = i.add(BigInteger.ONE)) {
            xr = ((((tempLambda.pow(2)).subtract(pubKey.getX()).subtract(tempX)).mod(primeNumber).add(primeNumber)).mod(primeNumber));
            yr = (((tempLambda.multiply(tempX.subtract(xr))).subtract(tempY)).mod(primeNumber).add(primeNumber)).mod(primeNumber); // (a % b + b) % b modulo for giving positive value (a%b give negative!)
            tempX = xr;
            tempY = yr;
            tempLambda = lambdaAddition(pubKey.getX(), pubKey.getY(), tempX, tempY);
        }
        Point u2Pubkey = new Point(xr,yr);

        //--------- addition of u1P and u2Pubkey
        BigInteger xp, yp, xq, yq;
	xp = u1P.getX();
	yp = u1P.getY();
	xq = u2Pubkey.getX();
	yq = u2Pubkey.getY();

	tempLambda = lambdaAddition(xp, yp, xq, yq);
	xr = ((( ((tempLambda.pow(2)).subtract(xp)).subtract(xq)).mod(primeNumber).add(primeNumber)).mod(primeNumber));
	yr = (((tempLambda.multiply(xp.subtract(xr))).subtract(yp)).mod(primeNumber).add(primeNumber)).mod(primeNumber);
        Point verificationPoint = new Point(xr,yr);

        if (((verificationPoint.getX()).mod(n)).compareTo(leftSignature) == 0) {
            System.out.println((verificationPoint.getX()).mod(n) + " = " + leftSignature);
            result = "FIX VERIFIED";
            return result;
        }
        else {
            System.out.println((verificationPoint.getX()).mod(n) + " = " + leftSignature);
            result = "NOT VERIFIED";
            return result;
        }
    }

    public Point computeSignature(String message, BigInteger priKey, Point pubKey) {

        //------- compute SHA1 ----------
        SHA1Algorithm sha1 = new SHA1Algorithm();
        String hashOutput = sha1.computeHash(message);

        //----------- compute digital signature elliptic curve -------------
        EllipticCurveDS digitalSignature = new EllipticCurveDS();
        Point signature = digitalSignature.validSignatureGeneration(hashOutput, priKey, pubKey);

        return signature;
    }

    public static void main(String[] args) {
        EllipticCurveDS digitalSignature = new EllipticCurveDS();

        //----------- compute private and public key -------------
	BigInteger priKey = generatePrivateKey();
        Point pubKey = digitalSignature.generatePublicKey(priKey);

        //----------- compute the signature of message -----------
        Point signature = digitalSignature.computeSignature(message, priKey, pubKey);

        //----------- verify the message ------------
        String verification = digitalSignature.signatureVerification(message, signature, pubKey);
        System.out.println(verification);
    }
}
