/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "<WebSig>" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2001, Institute for
 * Data Communications Systems, <http://www.nue.et-inf.uni-siegen.de/>.
 * The development of this software was partly funded by the European
 * Commission in the <WebSig> project in the ISIS Programme.
 * For more information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xml.security.algorithms.encryption;



import java.security.Provider;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import javax.crypto.ExemptionMechanism;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.w3c.dom.*;


/**
 *
 * @author $Author$
 */
public abstract class EncryptionMethodSpi {

   /** {@link org.apache.log4j} logging facility */
   static org.apache.log4j.Category cat =
      org.apache.log4j.Category.getInstance(EncryptionMethodSpi.class.getName());

   /** Field algorithm */
   protected javax.crypto.Cipher _cipherAlgorithm = null;

   /**
    * Method engineGetJCEAlgorithmString
    *
    * @return
    */
   protected String engineGetJCEAlgorithmString() {
      return this._cipherAlgorithm.getAlgorithm();
   }

   /**
    * Method engineGetJCEProviderName
    *
    * @return
    */
   protected String engineGetJCEProviderName() {
      return this._cipherAlgorithm.getProvider().getName();
   }

   /**
    * Method engineGetJCEProvider
    *
    * @return
    */
   protected Provider engineGetJCEProvider() {
      return this._cipherAlgorithm.getProvider();
   }

   /**
    * Method engineGetIV
    *
    * @return
    */
   protected byte[] engineGetIV() {
      return this._cipherAlgorithm.getIV();
   }

   /**
    * Method engineGetOutputSize
    *
    * @param inputLen
    * @return
    */
   protected int engineGetOutputSize(int inputLen) {
      return this._cipherAlgorithm.getOutputSize(inputLen);
   }

   /**
    * Method engineGetExemptionMechanism
    *
    * @return
    */
   protected ExemptionMechanism engineGetExemptionMechanism() {
      return this._cipherAlgorithm.getExemptionMechanism();
   }

   protected abstract byte[] engineUpdate(byte buf[])
           throws XMLSecurityException;
   protected abstract byte[] engineUpdate(byte buf[], int offset, int len) throws XMLSecurityException;

   /** Field _doc */
   Document _doc = null;

   /**
    * Method engineSetDocument
    *
    * @param doc
    */
   protected void engineSetDocument(Document doc) {
      this._doc = doc;
   }

   /** Field _constructionElement */
   Element _constructionElement = null;

   /**
    * Method engineGetContextFromElement
    *
    * @param element
    * @throws XMLSecurityException
    */
   protected void engineReadContextFromElement(Element element)
           throws XMLSecurityException {
      this._constructionElement = element;
   }

   /**
    * Method engineAddContextToElement
    *
    * @param element
    * @throws XMLSecurityException
    */
   protected void engineAddContextToElement(Element element)
           throws XMLSecurityException {}

   /**
    * Returns the Algorithm URI of the instantiated encryption method.
    *
    * @return the Algorithm URI of the instantiated encryption method.
    */
   protected abstract String engineGetURI();

   /**
    * Returns the key size which is implemented by the instantiated encryption method.
    *
    * @return the key size which is implemented by the instantiated encryption method.
    */
   protected abstract int  engineGetKeySize();
   protected abstract int  engineGetBlockSize();

   /**
    * Der init kann durchgeführt werden:
    *
    * init(Cipher.ENCRYPT_MODE, byte[] key, SecureRandom sr)
    * init(Cipher.ENCRYPT_MODE, byte[] key, byte[] iv)
    * init(Cipher.ENCRYPT_MODE, PublicKey pk,
    * init(Cipher.ENCRYPT_MODE, Certificate cert,
    * init(Cipher.DECRYPT_MODE, byte[] key,
    * init(Cipher.DECRYPT_MODE, PrivateKey pk,
    */
   protected abstract void engineInit(int opmode, byte[] key, SecureRandom sr) throws XMLSecurityException;
   protected abstract void engineInit(int opmode, byte[] key) throws XMLSecurityException;

   static {
      org.apache.xml.security.Init.init();
   }
}
