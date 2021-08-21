package kr.or.epro.fleaewha.controller;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.or.epro.fleaewha.dto.File2;
import kr.or.epro.fleaewha.dto.Product2;
import kr.or.epro.fleaewha.s3.AWSS3Service;
import kr.or.epro.fleaewha.service.FileService;
import kr.or.epro.fleaewha.service.PostService;

@RestController
public class ProductController {
	
	@Autowired
	private AWSS3Service service;
	    
	@Autowired
	private FileService fileService;
	
	@Autowired
	private PostService postService;
	
	@GetMapping("/products/{productID}")
	public Product2 getPost(
			@PathVariable int productID
	 ) throws Exception {	
		Product2 p = postService.getPost(productID);
		List files = fileService.getFiles(productID);
		p.setFiles(files);
		return p;
	 }
	
	@PostMapping("/new-product")
	public String addPost(
			@RequestPart(value= "file") final List<MultipartFile> multipartFiles,
			@RequestPart(value= "product") Product2 p
			) throws Exception {
		
		postService.addPost(p);
		
		String url;
		for(MultipartFile file : multipartFiles) {
			url = "https://fleaewhabucket.s3.ap-northeast-2.amazonaws.com/" + service.uploadFile(file);
			File2 file2 = new File2();
			file2.setProductID(p.getProductID());
			file2.setFileURL(url);
			fileService.addFile(file2);
		}
	
		return "post added";
	}


    @PutMapping("/modified-product")
    public String updatePost(
    		@RequestPart(value= "file") final List<MultipartFile> multipartFiles,
            @RequestPart(value= "product") Product2 p
    ) throws Exception {
    
    	postService.updatePost(p);
    
		String url;
		for(MultipartFile file : multipartFiles) {
			url = "https://fleaewhabucket.s3.ap-northeast-2.amazonaws.com/" + service.uploadFile(file);
			File2 file2 = new File2();
			file2.setProductID(p.getProductID());
			file2.setFileURL(url);
			fileService.updateFile(file2);
		}
    	
        return "post updated";
    }

    @DeleteMapping("/products/{productID}")
    public String deletePost(
            @PathVariable int productID
    ) throws Exception {
    	postService.deletePost(productID);
    	return "post deleted";
    }
	
}
