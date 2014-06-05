==Adapter JUnit tests==
The junit tests run on your adapter will test for conformance of naming conventions and other aspects required by the toolkit.


==How to Run the Junit Tests?==

===Configuration==
1.	Copy and paste all the targets under the  comment "JUNIT related targets START HERE" to  "END OF JUNIT RELATED TARGETS" in the build.xml of the sample-adapter project.
2. 	Copy adapter-interfaceTester.jar,hamcrest-all-1.3.jar,junit-4.10.jar and log4j-1.2.9.jar into your projects lib folder. The targets will use these jars to execute the tests.  
3.	Run the ant target 'infa-junit-test'. The build will fail with a message at the end. Open the ini file under infa_junit/INIFile and open the <uuid>.ini file. 
	Here uuid is the is the unique id of your plugin.     
	Parameters in the INI file:
	  	[Connection Parameters]
		Username = infa@testadapter.com
		Password = testadapterl00kup
		Here,  "Connection Parameters" section has the key-value pairs needed to make a successful connection to the adapter. 
		Your implementation may need more than these, like say "url".
	
		[Metadata Test]
		RandomSampleSize = 10  
		RecordsToBeTested = <comma separated record names>
		"RandomSampleSize" specifies maximum number of record names that will be tested for time consuming tests like MetaDataParameterizedTest.
		Example: If your implementation of IMetadata.getAllRecords() returns 100 Record objects, 10 will be selected randomly and tests will be run on them.
		"RecordsToBeTested" is considered only if "RandomSampleSize" is 0. Use this to test specific problematic Records. 
	
		[Runtime Test]	
		Refer to the instruction in the ini file to set the properties.	
				
===Running the ant task===		
Now run the ant target "infa-junit-test" again. This will run the tests and create the following files and folders:
1) ./infa_junit/junit_results has XML output of the tests run.
2) ./infa_junit/junit_reports  has junit results in a HTML format. Open the index.html to view the test reports.
3) ./infa_junit/Junit_Log.log has the logging information.

==Note==
1. WriteTest and ReadTest will always be a success in the JUnit reports. Verify the data manually.
2. Use 'ant infa-junit-clean' to delete the infa_junit directory.
	 
	
  
	   
