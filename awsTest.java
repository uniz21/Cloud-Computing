package org.yoonyj21.aws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesRequest;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeRegionsRequest;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.RebootInstancesResult;
import com.amazonaws.services.ec2.model.Region;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesResult;

public class awsTest {

	/*
	* Cloud Computing, Data Computing Laboratory
	* Department of Computer Science 
	* Chungbuk National University
	*/

	static AmazonEC2      ec2;

	private static void init() throws Exception {

		/*
		* The ProfileCredentialsProvider will return your [default]
		* credential profile by reading from the credentials file located at
		* (~/.aws/credentials).
		*/
		ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
		try {
			credentialsProvider.getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException(
				"Cannot load the credentials from the credential profiles file. " +
				"Please make sure that your credentials file is at the correct " +
				"location (~/.aws/credentials), and is in valid format.",
e);
		}
		ec2 = AmazonEC2ClientBuilder.standard()
			.withCredentials(credentialsProvider)
			.withRegion("ap-northeast-2") /* check the region at AWS console */
			.build();
	}

	public static void main(String[] args) throws Exception {

		init();

		Scanner menu = new Scanner(System.in);
		Scanner id_string = new Scanner(System.in);
		int number = 0;
		while(true)
		{
			System.out.println("                                                            ");
			System.out.println("                                                            ");
			System.out.println("------------------------------------------------------------");
			System.out.println("           Amazon AWS Control Panel using SDK               ");
			System.out.println("                                                            ");
			System.out.println("  Cloud Computing, Computer Science Department              ");
			System.out.println("                           at Chungbuk National University  ");
			System.out.println("------------------------------------------------------------");
			System.out.println("  1. list instance                2. available zones         ");
			System.out.println("  3. start instance               4. available regions      ");
			System.out.println("  5. stop instance                6. create instance        ");
			System.out.println("  7. reboot instance              8. list images            ");
			System.out.println("  9. delete instance             10. create multi instance  ");
			System.out.println("                                 99. quit                   ");
			System.out.println("------------------------------------------------------------");

			System.out.print("Enter an integer: ");
			Scanner scan = new Scanner(System.in);
			number = scan.nextInt();

			switch(number) {
			case 1: 
				listInstances();
				break;
			case 2:
				availableZones();
				break;
			case 3:
				startInstance();
				break;
			case 4:
				availableRegions();
				break;
			case 5:
				stopInstance();
				break;
			case 6:
				createInstance();
				break;
			case 7:
				rebootInstance();
				break;
			case 8:
				listImages();
				break;
			case 9:
				deleteInstance();
				break;
			case 10:
				createMultiInstance();
				break;
			case 99:
				return;
			}
		}
	}

	public static void listInstances()
	{

		System.out.println("Listing instances....");
		boolean done = false;

		DescribeInstancesRequest request = new DescribeInstancesRequest();
		while(!done) {
			DescribeInstancesResult response = ec2.describeInstances(request);

			for(Reservation reservation : response.getReservations()) {
				for(Instance instance : reservation.getInstances()) {
					System.out.printf(
						"[id] %s, " +
						"[AMI] %s, " +
						"[type] %s, " +
						"[state] %10s, " +
						"[monitoring state] %s",
						instance.getInstanceId(),
						instance.getImageId(),
						instance.getInstanceType(),
						instance.getState().getName(),
						instance.getMonitoring().getState());
				}
				System.out.println();
			}

			request.setNextToken(response.getNextToken());

			if(response.getNextToken() == null) {
				done = true;

			}
		}
	}

	public static void availableZones()
	{

		System.out.println("Available zones....");
		boolean done = false;

		DescribeAvailabilityZonesRequest req = new DescribeAvailabilityZonesRequest();
		while(!done) {
			DescribeAvailabilityZonesResult response = ec2.describeAvailabilityZones(req);;

			for(AvailabilityZone availZone : response.getAvailabilityZones()) {
				System.out.printf(
					"[id] %s, " +
					"[region] %s, " +
					"[zone] %s, ",
					availZone.getZoneId(),
					availZone.getRegionName(),
					availZone.getZoneName());
				System.out.println();
			}

			done = true;
		}
	}
	
	public static void availableRegions()
	{

		System.out.println("Available regions....");
		boolean done = false;

		DescribeRegionsRequest req = new DescribeRegionsRequest();
		while(!done) {
			DescribeRegionsResult response = ec2.describeRegions(req);

			for(Region region : response.getRegions()) {
				System.out.printf(
					"[region] %15s, " +
					"[endpoint] %s, ",
					region.getRegionName(),
					region.getEndpoint());
				System.out.println();
			}

			done = true;
		}
	}

	public static void startInstance()
	{
		List<String> ids = new ArrayList<String>();
		
		System.out.print("Enter instance id: ");
		Scanner scan = new Scanner(System.in);
		ids.add(scan.nextLine());
		
		System.out.println("Starting .... "+ids.get(0));
		StartInstancesRequest req = new StartInstancesRequest(ids);
		try {
			StartInstancesResult result = ec2.startInstances(req);
			System.out.println("Successfully started instance "+ids.get(0));
		}catch(Exception e)
		{
			System.out.println("Wrong id");
		}
	}

	
	public static void stopInstance()
	{
		List<String> ids = new ArrayList<String>();
		
		System.out.print("Enter instance id: ");
		Scanner scan = new Scanner(System.in);
		ids.add(scan.nextLine());
		
		System.out.println("Starting .... "+ids.get(0));
		StopInstancesRequest req = new StopInstancesRequest(ids);
		try {
			StopInstancesResult result = ec2.stopInstances(req);
			System.out.println("Successfully stopped instance "+ids.get(0));
		}catch(Exception e)
		{
			System.out.println("Wrong id");
		}
	}
	
	public static void rebootInstance()
	{
		List<String> ids = new ArrayList<String>();
		
		System.out.print("Enter instance id: ");
		Scanner scan = new Scanner(System.in);
		ids.add(scan.nextLine());
		
		System.out.println("Starting .... "+ids.get(0));
		RebootInstancesRequest req = new RebootInstancesRequest(ids);
		try {
			RebootInstancesResult result = ec2.rebootInstances(req);
			System.out.println("Successfully rebooted instance "+ids.get(0));
		}catch(Exception e)
		{
			System.out.println("Wrong id or instance is stopped");
		}
	}
	
	public static void createInstance()
	{
		String image_id;
		boolean done = false;
		
		System.out.print("Enter ami id: ");
		Scanner scan = new Scanner(System.in);
		image_id = scan.nextLine();
		
		System.out.println("Starting .... "+image_id);
		RunInstancesRequest req = new RunInstancesRequest(image_id,1,1);
		try {
			RunInstancesResult result = ec2.runInstances(req);
			System.out.println("Successfully started EC2 instance "+result.getReservation().getInstances().get(0).getInstanceId()+image_id);
		}catch(Exception e)
		{
			System.out.println("Wrong id");
		}
	}
	
	public static void createMultiInstance()
	{
		String image_id;
		int inst_num=0;
		boolean done = false;
		
		System.out.print("Enter ami id: ");
		Scanner scan = new Scanner(System.in);
		image_id = scan.nextLine();
		System.out.print("Enter instance number(larger than 0) to create: ");
		while(inst_num<1)
		{
			inst_num = new Scanner(System.in).nextInt();
		}
		
		System.out.println("Starting .... "+image_id);
		RunInstancesRequest req = new RunInstancesRequest(image_id,1,inst_num);
		try {
			RunInstancesResult result = ec2.runInstances(req);
			for(Instance instance : result.getReservation().getInstances())
			{
				System.out.println("Successfully started EC2 instance "+instance.getInstanceId());
			}
		}catch(Exception e)
		{
			System.out.println("Wrong id");
		}
	}
	
	public static void listImages()
	{
		List<String> owner = new ArrayList<String>();
		System.out.println("Listing images....");
		owner.add("811125161057");//if owner is null -> public images + private images

		DescribeImagesRequest request = new DescribeImagesRequest();
		request.setOwners(owner);
		DescribeImagesResult response = ec2.describeImages(request);
		for(Image images : response.getImages()) {
			System.out.printf(
				"[ImageID] %s, " +
				"[Name] %s, " +
				"[Owner] %s, ",
				images.getImageId(),
				images.getName(),
				images.getOwnerId());
			System.out.println();
		}
	}
	
	public static void deleteInstance()
	{
		List<String> ids = new ArrayList<String>();
		
		System.out.print("Enter instance id: ");
		Scanner scan = new Scanner(System.in);
		ids.add(scan.nextLine());

		System.out.println("Terminating .... "+ids.get(0));
		TerminateInstancesRequest req = new TerminateInstancesRequest();
		req.setInstanceIds(ids);
		try {
			TerminateInstancesResult result = ec2.terminateInstances(req);
			System.out.println("Successfully terminated instance "+ids.get(0));
		}catch(Exception e)
		{
			System.out.println("Wrong id");
		}
	}
}
