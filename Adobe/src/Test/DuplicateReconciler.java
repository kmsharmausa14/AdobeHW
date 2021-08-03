package Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import com.google.gson.Gson;

public class DuplicateReconciler {

	public static void main(String[] args) {
		DuplicateReconciler duplicateReconciler = new DuplicateReconciler();

		// Enter input file path
		String inputPath = System.getProperty("user.dir") + "\\leads.json";

		// Enter output file path
		String outputPath = System.getProperty("user.dir") + "\\output.json";

		duplicateReconciler.processJson(inputPath, outputPath);
	}

	private void processJson(String inputPath, String outputPath) {
		List<Lead> leads = new ArrayList<>();
		DuplicateReconciler duplicateReconciler = new DuplicateReconciler();
		DateTimeFormatter f = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

		try (FileReader reader = new FileReader(inputPath)) {

			Gson gson = new Gson();
			Root root = gson.fromJson(reader, Root.class);

			for (Lead lead : root.getLeads()) {
				if (leads.size() == 0)
					leads.add(lead);
				else {
					for (Lead resultLead : leads) {
						if (resultLead.getId().equals(lead.getId()) || resultLead.getEmail().equals(lead.getEmail())) {
							LocalDateTime start = LocalDateTime.parse(lead.getEntryDate(), f);
							LocalDateTime stop = LocalDateTime.parse(resultLead.getEntryDate(), f);
							if (stop.isBefore(start) || stop.isEqual(start)) {
								duplicateReconciler.logChanges(resultLead, lead);
								leads.remove(resultLead);
								break;
							}
						}
					}
					leads.add(lead);
				}
			}

			root = new Root();
			root.setLeads(leads);
			Writer writer = Files.newBufferedWriter(Paths.get(outputPath));
			gson.toJson(root, writer);
			// close writer
			writer.close();
		}

		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void logChanges(Lead oldData, Lead newData) {
		System.out.println("Duplicate Records Changes");
		if (!oldData.getId().equals(newData.getId()))
			System.out.println("Id Changed from " + oldData.getId() + " to " + newData.getId());
		else
			System.out.println("Id : " + oldData.getId());

		if (!oldData.getEmail().equals(newData.getEmail()))
			System.out.println("Email Changed from " + oldData.getEmail() + " to " + newData.getEmail());
		else
			System.out.println("Email : " + oldData.getEmail());

		if (!oldData.getFirstName().equals(newData.getFirstName()))
			System.out.println("First Name Changed from " + oldData.getFirstName() + " to " + newData.getFirstName());
		else
			System.out.println("First Name : " + oldData.getFirstName());

		if (!oldData.getLastName().equals(newData.getLastName()))
			System.out.println("LastName Changed from " + oldData.getLastName() + " to " + newData.getLastName());
		else
			System.out.println("LastName : " + oldData.getLastName());

		if (!oldData.getAddress().equals(newData.getAddress()))
			System.out.println("Address Changed from " + oldData.getAddress() + " to " + newData.getAddress());
		else
			System.out.println("Address : " + oldData.getAddress());

		if (!oldData.getEntryDate().equals(newData.getEntryDate()))
			System.out.println("EntryDate Changed from " + oldData.getEntryDate() + " to " + newData.getEntryDate());
		else
			System.out.println("EntryDate : " + oldData.getEntryDate());

		System.out.println();

	}
}
