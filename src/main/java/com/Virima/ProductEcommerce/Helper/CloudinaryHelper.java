package com.Virima.ProductEcommerce.Helper;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Component
public class CloudinaryHelper {

    @Value("${CLOUDINARY_URL}")
    String url;
    public String saveImage(MultipartFile file) {
        Cloudinary cloudinary = new Cloudinary(url);
        Map map = null;
        try {
            map = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("folder", "ecommerce"));
        } catch (IOException e) {
        }
        return (String) map.get("url");
    }

    public String savePost(MultipartFile file)
    {
        Cloudinary cloudinary=new Cloudinary(url);
        Map map=null;
        try {
            map=cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("folder","ecommerce"));
        }catch(IOException e)
        {

        }
        return (String) map.get("url");
    }

    public void deletePost(String public_id)
    {
        Cloudinary cloudinary=new Cloudinary();
        try {
            cloudinary.uploader().destroy(public_id, ObjectUtils.emptyMap());
            System.out.println("The post is deleted Successfully");
        }catch (Exception e) {

        }
    }
    public String getPublicIdFromUrl(String cloudinaryUrl) {
        // Split the URL by '/' and get the part before the file extension
        String[] urlParts = cloudinaryUrl.split("/");
        String lastPart = urlParts[urlParts.length - 1];  // Get the last part of the URL
        String[] publicIdAndExtension = lastPart.split("\\.");  // Split by the dot to separate public ID and file extension
        return publicIdAndExtension[0];  // Return the public ID (before the extension)
    }
}
