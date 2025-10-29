package co.mannit.commonservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.bson.Document;
import org.apache.poi.ss.util.CellAddress;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;


@RestController
public class UploadMarks {

	
	@Autowired
	private MongoTemplate mongoTemplate;
	@PostMapping("/Upload/marks/v.2")
	public ResponseEntity<String> getok(@RequestPart(name="file") MultipartFile file,@RequestParam(required=false) String subject,@RequestParam (required=false) String Class,@RequestParam(required=false)  String exam_name) {
	
	    try (InputStream fis = file.getInputStream();
	             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

	            XSSFSheet sheet = workbook.getSheetAt(0);
	       
	           // MongoCollection<Document> collection = database.getCollection(Class+"_"+subject+"_"+exam_name+"_"+"Questions");
	            
       
	            Map<Integer, List<Document>> imageMap = extractImages2(sheet, workbook);
	            for (Row row : sheet) {
	            	if(sheet.getFirstRowNum()==row.getRowNum()) {
	            		continue;
	            	}
	            	 if (isRowEmpty(row)) continue;  
	            	 
	            		Document doc = new Document();
//	            	System.out.println(row.getRowNum()+"   ");
	            		if(!(getCellValue2(row.getCell(0)).isEmpty()||getCellValue2(row.getCell(0)).isBlank())) {
	            			String cellValue = getCellValue2(row.getCell(0)); // Always returns a String

	            			if (cellValue.matches("\\d+")) {  // Check if it's a whole number
	            			    doc.append("QID", Integer.parseInt(cellValue)); 
	            			} else {
	            			    doc.append("QID", cellValue); // Store it as a string if it's not a number
	            			}
	            			
	            		}
	            		if(!(getCellValue2(row.getCell(1)).isEmpty()||getCellValue2(row.getCell(1)).isBlank())) {
	            			doc.append("Question", getCellValue2(row.getCell(1)));
	            		}
		                
	            		if(!(getCellValue2(row.getCell(2)).isEmpty()||getCellValue2(row.getCell(2)).isBlank())) {
	            			 doc.append("OPT1", getCellValue2(row.getCell(2)));
	            		}
	            		if(!(getCellValue2(row.getCell(3)).isEmpty()||getCellValue2(row.getCell(3)).isBlank())) {
	            			doc.append("OPT2", getCellValue2(row.getCell(3)));
	            		}
	            		if(!(getCellValue2(row.getCell(4)).isEmpty()||getCellValue2(row.getCell(4)).isBlank())) {
	            			 doc.append("OPT3", getCellValue2(row.getCell(4)));
	            		}
	            		if(!(getCellValue2(row.getCell(5)).isEmpty()||getCellValue2(row.getCell(5)).isBlank())) {
	            			doc.append("OPT4", getCellValue2(row.getCell(5)));
	            		}
	            		if(!(getCellValue2(row.getCell(6)).isEmpty()||getCellValue2(row.getCell(6)).isBlank())) {
	            			 doc.append("OPT5", getCellValue2(row.getCell(6)));
	            		}
	            		
	            		if(!(getCellValue2(row.getCell(7)).isEmpty()||getCellValue2(row.getCell(7)).isBlank())) {
	            			doc.append("ANSWERS", getCellValue2(row.getCell(7)));
	            		}
	            		
		               
		                
		                doc.append("IMAGE", getCellValue2(row.getCell(8)));
		                if(!(getCellValue2(row.getCell(9)).isEmpty()||getCellValue2(row.getCell(9)).isBlank())) {
		                	doc.append("Categories".toUpperCase(),getCellValue2(row.getCell(9)));
	            		}
		                if(!(getCellValue2(row.getCell(10)).isEmpty()||getCellValue2(row.getCell(10)).isBlank())) {
		                	doc.append("Topic".toUpperCase(),getCellValue2(row.getCell(10)));
	            		}
		                if(!(getCellValue2(row.getCell(11)).isEmpty()||getCellValue2(row.getCell(11)).isBlank())) {
		                	 doc.append("subtopic".toUpperCase(),getCellValue2(row.getCell(11)));
	            		}
		                
		                if(!(getCellValue2(row.getCell(12)).isEmpty()||getCellValue2(row.getCell(12)).isBlank())) {
		                	 doc.append("chapter".toUpperCase(), (int)Double.parseDouble(getCellValue2(row.getCell(12))));
	            		}
		                
		                if(!(getCellValue2(row.getCell(13)).isEmpty()||getCellValue2(row.getCell(13)).isBlank())) {
		                	 doc.append("solutions".toUpperCase(), getCellValue2(row.getCell(13)));
	            		}
		                
		                	 doc.append("EXACTANSWER".toUpperCase(), getCellValue2(row.getCell(14)));

						/*
						 * for (Cell cell : row) { String cellValue = getCellValue2(cell);
						 * doc.append("OPT" + cell.getColumnIndex(), cellValue); }
						 */
		             /*   if (imageMap.containsKey(row.getRowNum())) {
		                    doc.append("image", imageMap.get(row.getRowNum()));
		                }*/
						 List<Document> imageList = new ArrayList<>();
						 if (imageMap.containsKey(row.getRowNum())) {
			                    for (int i = 0; i < imageMap.get(row.getRowNum()).size(); i++) {
			                        Document imageDoc = imageMap.get(row.getRowNum()).get(i);

                                  
			                        imageList.add(imageDoc);
			                    }
			                    doc.append("images".toUpperCase(), imageList);

		                
		             
	            	
	                
						 }
						 doc.append("subject".toUpperCase(), subject);
						 doc.append("class".toUpperCase(), Class);
						//   mongoTemplate.insert(doc);
						   if(exam_name!=null) {
							   mongoTemplate.save(doc,Class+"_"+subject+"_"+exam_name+"_"+"Questions");
						   }else {
							   mongoTemplate.save( doc,Class+"_"+subject+"_"+"Questions");
						   }
						
						 //apiService.updateDoc(subject, Class, mongoDbName, doc.toJson(),Class+"_"+subject+"_"+exam_name+"_"+"Questions");
						 
						 //apiService.updateDoc(subject, Class, mongoDbName, doc.toJson(),Class+"_"+subject+"_"+"Questions");
						 }
	            
	            
//	            System.out.println("Excel data inserted into MongoDB!");
	            
	            return ResponseEntity.ok().body("Excel data inserted into MongoDB!");

	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.internalServerError().body(e.getMessage());
	        }
		
	    }

	    private static boolean isRowEmpty(Row row) {
	        if (row == null) return true;  // Row is completely null

	        for (Cell cell : row) {
	            if (cell.getCellType() != CellType.BLANK) { 
	                return false;  // Found a non-empty cell
	            }
	        }
	        return true;  // All cells were empty
	    }

	    private static Map<Integer, List<Document>> extractImages2(XSSFSheet sheet, XSSFWorkbook workbook) throws IOException {
	    	 Map<Integer, List<Document>> imageMap = new HashMap<>();
	         XSSFDrawing drawing = sheet.getDrawingPatriarch();
	         if (drawing == null) return imageMap;

	         for (XSSFShape shape : drawing.getShapes()) {
	             if (shape instanceof XSSFPicture) {
	                 XSSFPicture picture = (XSSFPicture) shape;
	                 XSSFPictureData pictureData = picture.getPictureData();
	                 ClientAnchor anchor = picture.getPreferredSize();
	                 int row = anchor.getRow1();
	                 int col = anchor.getCol1(); // Column index
                     
	                 byte[] imageBytes = pictureData.getData();
	                 String base64Image = Base64.encodeBase64String(imageBytes);

	                 // Store multiple images per row with column mapping
	                 
	                 Document imageDoc = new Document("column", col);
	                 if(col==1) {
	                	 imageDoc.append("Question", base64Image) ;
	                 }
	                 if(col==2) {
	                	 imageDoc.append("OPT1", base64Image) ;
	                	 
	                 } if(col==3) {
	                	 imageDoc.append("OPT2", base64Image) ;
	                	 
	                 }
	                 if(col==4) {
	                	 imageDoc.append("OPT3", base64Image) ;
	                	 
	                 } if(col==5) {
	                	 imageDoc.append("OPT4", base64Image) ;
	                	 
	                 }
	                 if(col==6) {
	                	 imageDoc.append("OPT5", base64Image) ;
	                	 
	                 }
	                 if(col==7) {
	                	 imageDoc.append("ANSWERS", base64Image) ;
	                	 
	                 } if(col==8) {
	                	 imageDoc.append("IMAGEE", base64Image) ;
	                	 
	                 }if(col==13) {
	                	 imageDoc.append("SOLUTIONS", base64Image) ;
	                 }
	    
	                 imageMap.computeIfAbsent(row, k -> new ArrayList<>()).add(imageDoc);
	             }
	         }
	         return imageMap;
	    }

	    private String getCellValue2(Cell cell) {
	        if (cell == null) {
	            return "";
	        }
	        switch (cell.getCellType()) {
	            case STRING:
	                return cell.getRichStringCellValue().getString().trim(); // Handles rich text (subscripts, superscripts)
	            case NUMERIC:
	                if (DateUtil.isCellDateFormatted(cell)) {
	                    return cell.getDateCellValue().toString();
	                }
	                
	                double numericValue = cell.getNumericCellValue();
	             
	                if (numericValue == Math.floor(numericValue)) {
	                    return String.valueOf((int) numericValue);
	                } else {
	                    return String.valueOf(numericValue);
	                }
	            case BOOLEAN:
	                return Boolean.toString(cell.getBooleanCellValue());
	            case FORMULA:
	                try {
	                    return cell.getStringCellValue();
	                } catch (IllegalStateException e) {
	                    return String.valueOf(cell.getNumericCellValue());
	                }
	            case BLANK:
	                return "";
	            default:
	                return "";
	        }
	    }

	
}
