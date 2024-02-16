package HospitalManagementSystem;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.Connection;

public class HospitalManagementSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hms_project";
    private static final String username = "root";
    private static final String password = "Saman";
    
    public static void main(String[] args) {
		try {
			Scanner scanner = new Scanner(System.in);
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(url, username, password);
			Patient patient = new Patient(scanner, connection);
			Doctor doctor = new Doctor(connection);
			
			while(true) {
				System.out.println("---HOSPITAL MANAGEMENT SYSTEM---");
				System.out.println("Enter Your Choice To Proceed:");
				System.out.println("1. Add Patient");
				System.out.println("2. View Patient");
				System.out.println("3. View Doctors");
				System.out.println("4. Book An Appointment");
				System.out.println("5. Exit");
				int choice = scanner.nextInt();
				
				switch(choice) {
				  case 1:
					  //Add Patient
					  patient.addPatient();
					  System.out.println();
					  break;
				  case 2:
					  //View Patient
					  patient.viewPatient();
					  System.out.println();
					  break;
				  case 3:
					  //View Doctor
					  doctor.viewDoctor();
					  System.out.println();
					  break;
				  case 4:
					  //Book appointment
					  bookAppointment(patient, doctor, connection, scanner);
					  System.out.println();
					  break;
				  case 5:
					  System.out.println("!!Thank You for using Hospital Management System!!");
					  return;
				  default :
					  System.out.println("!!Enter  valid choice!!");	  
				}
			}
		}catch(ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
    public static void bookAppointment(Patient patient,Doctor doctor,Connection connection, Scanner scanner) {
        System.out.println("Enter Patient Id:");
        int patientId = scanner.nextInt();
        System.out.println("Enter Doctor Id:");
        int doctorId = scanner.nextInt();
        System.out.println("Enter Appointment Date(YYYY-MM-DD):");
        String appointmentDate = scanner.next();
        
        if(patient.checkPatientById(patientId) && doctor.checkDoctorById(doctorId)) {
        	
        	if(checkDoctorAvailability(doctorId, appointmentDate, connection)) {
        		String appointmentQuery = "INSERT INTO appointments(pats_id, docs_id, appointment_date) values(?, ?, ?)";
        	try {
        		PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
        		preparedStatement.setInt(1, patientId);
        		preparedStatement.setInt(2, doctorId);
        		preparedStatement.setString(3, appointmentDate);
        		int rowsAffected = preparedStatement.executeUpdate();
        		if(rowsAffected>0) {
        			System.out.println("Appointment Booked!");
        		}else {
        			System.err.println("Failed To Book Appointment");
        		}
        	}catch(SQLException e) {
        		e.printStackTrace();
        	}
        	}else {
        		System.out.println("Doctor Not Avaialable On This Date");
        	}
        }else {
        	System.err.println("Either Doctor Or Patient Does Not Exist");
        }
    }
        
        public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection) {
        	String query = "SELECT COUNT(*) FROM appointments WHERE docs_id = ? AND appointment_date = ?";
        	try {
        		PreparedStatement preparedStatement = connection.prepareStatement(query);
        		preparedStatement.setInt(1, doctorId);
        		preparedStatement.setString(2, appointmentDate);
        		ResultSet resultSet = preparedStatement.executeQuery();
        		if(resultSet.next()) {
        			int count = resultSet.getInt(1);
        			if(count==0) {
        				return true;
        			}else {
        				return false;
        			}
        		}
        	}catch (SQLException e) {
        		e.printStackTrace();
        	}
        	return false;
        }
}
