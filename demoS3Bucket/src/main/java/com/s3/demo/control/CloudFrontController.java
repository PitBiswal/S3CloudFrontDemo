package com.s3.demo.control;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.cloudfront.CloudFrontUrlSigner;
import com.amazonaws.services.cloudfront.util.SignerUtils.Protocol;

@RestController
@RequestMapping("/cloudfront")
public class CloudFrontController {

	@Autowired
	private Environment env;
	// private String secretKey="o88b0eJSLZmLcCAKqhCFp4mCzT9JcYVlkWvM/c9O";
	// the DNS name of your CloudFront distribution, or a registered alias
	// String distributionDomainName="dwrczo2enqk42.cloudfront.net";
	// the private key you created in the AWS Management Console
	// File cloudFrontPrivateKeyFile=new
	// File("D:\\aws\\pk-APKA4CYRPRBDZJEEMGOQ.pem");
	// the unique ID assigned to your CloudFront key pair in the console
	// String cloudFrontKeyPairId="APKA4CYRPRBDZJEEMGOQ";
	// Date expirationDate = new Date(120000000000000);
	// String s3Key="book.xlsx";

	@GetMapping("/s3file/{fname}")
	public ResponseEntity<Object> getS3BucketFile(@PathVariable("fname") String s3fname) {
		String url0 = null;
		String distributionDomainName = env.getProperty("distributionDomainName");
		File cloudFrontPrivateKeyFile = new File(env.getProperty("cloudFrontPrivateKeyFile"));
		String cloudFrontKeyPairId = env.getProperty("cloudFrontKeyPairId");
		try {
			System.out.println(s3fname);
			url0 = CloudFrontUrlSigner.getSignedURLWithCannedPolicy(Protocol.https, // protocol
					distributionDomainName, // distributionDomain
					cloudFrontPrivateKeyFile, // privateKey
					s3fname, // objectKey
					cloudFrontKeyPairId, // keyId
					new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24) // endDate
			);
			System.out.println(url0);

		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		URI cf = null;
		try {
			cf = new URI(url0);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(cf);
		return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);

	}

}
