/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rsa;
import java.math.BigInteger;

import java.util.Random;
/**
 *
 * @author ahmed
 */
public class rs {
    private static final BigInteger ZERO = BigInteger.ZERO;
    private static final BigInteger ONE = BigInteger.ONE;
    private static final BigInteger TWO = new BigInteger("2");
    private static final BigInteger THREE = new BigInteger("3");
    public static final int[] aValues = { 2, 3 };
    
    BigInteger q,p,n,phi,e,d;
    
    rs()
    {
        do
        {
         Random rng=new  Random();
         q=new BigInteger(512,rng);
        }while(! millerRabin(q,177));
        
        do
        {
         Random rng=new  Random();
         p=new BigInteger(512,rng);
        }while(! millerRabin(p,177));
        
        n=q.multiply(p);
        
        phi= (q.subtract(ONE)).multiply(p.subtract(ONE));
        
        e= new BigInteger("65537");
        
        d= e.modInverse(phi);
    }
     public String encrypt(String plainText)
    {
        BigInteger msg = new BigInteger(plainText.getBytes());
        byte[] encrypted =  getSquareAndMultipyResult(msg,e, n).toByteArray();
        return toHex(encrypted);
    }
    public String decrypt(String cipherText)
    {
        BigInteger encrypted = new BigInteger(cipherText, 16);
        return new String( crt(encrypted).toByteArray());
    }
     private String toHex(byte[] bytes)
    {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
    }
     public BigInteger getSquareAndMultipyResult(BigInteger baseElement,BigInteger exponent,BigInteger modulus) {
		byte[] expBinaryArray=exponent.toString(2).getBytes();
		BigInteger result=baseElement;
		for(int i=1;i<expBinaryArray.length;++i) {
                        result=result.multiply(result);
			result=result.mod(modulus);
			if(expBinaryArray[i]=='1') {
                               result=result.multiply(baseElement);
			result=result.mod(modulus);
			}
		}
		return result;
	}
      public BigInteger crt(BigInteger y)
      {
          BigInteger yp,yq,dp,dq,xp,xq,cp,cq,x;
          yp=y.mod(p);
          yq=y.mod(q);
          dp=d.mod(p.subtract(ONE));
           dq=d.mod(q.subtract(ONE));
           xp=getSquareAndMultipyResult(yp,dp,p);
            xq=getSquareAndMultipyResult(yq,dq,q);
            cp=q.modInverse(p);
            cq=p.modInverse(q);
            x=(q.multiply(cp)).multiply(xp);
            x=x.add((p.multiply(cq)).multiply(xq));
            x=x.mod(n);
            return x;
      }
    public static boolean testPr(BigInteger n, BigInteger a, int s, BigInteger d) {
		for (int i = 0; i < s; i++) {
			BigInteger exp = TWO.pow(i);
			exp = exp.multiply(d);
			BigInteger res = a.modPow(exp, n);
			if (res.equals(n.subtract(ONE)) || res.equals(ONE)) {
				return true;
			}
		}
		return false;
	}
    public static boolean millerRabin(BigInteger n, int numValues) {
		BigInteger d = n.subtract(ONE); 
		int s = 0;
		while (d.mod(TWO).equals(ZERO)) {
			s++;
			d = d.divide(TWO);
		}
		for (int i = 0; i < numValues; i++) { 
			BigInteger a =uniformRandom(TWO,n.subtract(TWO)); 
			boolean r = testPr(n, a, s, d);
			if (!r) {
				return false;
			}
		}
		return true;
	}
     private static BigInteger uniformRandom(BigInteger bottom, BigInteger top) {
        Random rnd = new Random();
        BigInteger res;
        do {
            res = new BigInteger(top.bitLength(), rnd);
        } while (res.compareTo(bottom) < 0 || res.compareTo(top) > 0);
        return res;
    }
}