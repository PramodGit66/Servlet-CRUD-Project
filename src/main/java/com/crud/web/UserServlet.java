package com.crud.web;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.crud.dao.UserDAO;
import com.crud.model.User;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@WebServlet("/")
public class UserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private UserDAO userDAO;

	public UserServlet() {
		this.userDAO = new UserDAO();
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getServletPath();

		switch (action) {
		case "/new":
			showNewForm(request, response);
			break;
		case "/insert":
			try {
				insertUser(request, response);
			} catch (SQLException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case "/delete":
			try {
				deleteUser(request, response);
			} catch (SQLException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case "/edit":
			try {
				showEditForm(request, response);
			} catch (SQLException | ServletException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case "/update":
			try {
				updateUser(request, response);
			} catch (SQLException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case "/pdfreport":
			   try {
				getPdfReport(request, response);
			} catch (SQLException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case "/excelreport"	:
			try {
				getExcelReport(request, response);
			} catch (SQLException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		default:
			try {
				listUser(request, response);
			} catch (SQLException | IOException | ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}

	}

	private void showNewForm(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		RequestDispatcher dispatcher = request.getRequestDispatcher("user-form.jsp");
		dispatcher.forward(request, response);
	}

	private void insertUser(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
		String name = request.getParameter("name");
		String city = request.getParameter("city");
		User newUser = new User(name, city);
		userDAO.insertUser(newUser);
		response.sendRedirect("user-list");
	}

	private void deleteUser(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
		int id = Integer.parseInt(request.getParameter("id"));
		userDAO.deleteUser(id);
		response.sendRedirect("user-list");
	}

	private void showEditForm(HttpServletRequest request, HttpServletResponse response)
			throws SQLException, ServletException, IOException {
		int id = Integer.parseInt(request.getParameter("id"));
		User existingUser = userDAO.selectUser(id);
		RequestDispatcher dispatcher = request.getRequestDispatcher("user-form.jsp");
		request.setAttribute("user", existingUser);
		dispatcher.forward(request, response);
	}

	private void updateUser(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
		int id = Integer.parseInt(request.getParameter("id"));
		String name = request.getParameter("name");
		String city = request.getParameter("city");

		User user = new User(id, name, city);
		userDAO.updateUser(user);
		response.sendRedirect("user-list");
	}
	
	private void getPdfReport(HttpServletRequest req, HttpServletResponse resp)throws SQLException, IOException{
		 resp.setContentType("application/pdf");
         resp.setHeader("Content-Disposition", "attachment; filename=users_report.pdf");
         
         Document document = new Document();
         try {
             PdfWriter.getInstance(document, resp.getOutputStream());
             document.open();
				
				  String logopath = getServletContext().getRealPath("/brand.jpg"); Image logo =
				  Image.getInstance(logopath); logo.scaleToFit(50, 50);
				  logo.setAlignment(Image.ALIGN_LEFT); document.add(logo);
				 
             

             // Add Title
             Font boldFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
             Paragraph title = new Paragraph("User Report", boldFont);
             title.setAlignment(Element.ALIGN_CENTER);
             document.add(title);
             
             document.add(new Paragraph(" ")); // Add a blank line

             // Create Table
             PdfPTable table = new PdfPTable(3);
             table.setWidthPercentage(100);
             table.setSpacingBefore(10f);
             table.setSpacingAfter(10f);
             
             // Add Table Header
             addTableHeader(table);
             
             List<User> users = userDAO.selectAllUsers();
             for(User user: users) {
          	   table.addCell(String.valueOf(user.getId()));
                 table.addCell(user.getName());                   
                 table.addCell(user.getCity());
             }
             
             document.add(table);
             document.close();
             
         }catch (DocumentException e) {
             e.printStackTrace();
         }
         
		
	}
    
	private void getExcelReport(HttpServletRequest req, HttpServletResponse resp)throws SQLException, IOException{
		resp.setContentType("application/vnd.ms-excel");
		resp.setHeader("Content-Disposition", "attachment; filename=employee_report.xlsx");
		try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Employees");

            // Header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Name");           
            headerRow.createCell(2).setCellValue("City");
            
            List<User> users = userDAO.selectAllUsers();
            int rowCount=1;
            for(User user: users) {
            	Row row = sheet.createRow(rowCount++);
            	row.createCell(0).setCellValue(user.getId());
                row.createCell(1).setCellValue(user.getName());
                row.createCell(2).setCellValue(user.getCity());
            }
            
            workbook.write(resp.getOutputStream());
         	   
            }
            
            
		
	}
	
	private void listUser(HttpServletRequest request, HttpServletResponse response)
			throws SQLException, IOException, ServletException {
		List<User> listUser = userDAO.selectAllUsers();
		request.setAttribute("listUser", listUser);
		RequestDispatcher dispatcher = request.getRequestDispatcher("user-list.jsp");
		dispatcher.forward(request, response);
	}
	
	private void addTableHeader(PdfPTable table) {
        PdfPCell header1 = new PdfPCell(new Phrase("ID"));
        header1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(header1);

        PdfPCell header2 = new PdfPCell(new Phrase("Name"));
        header2.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(header2);

        PdfPCell header4 = new PdfPCell(new Phrase("City"));
        header4.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(header4);
    }

}
