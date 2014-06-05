Sample Toolkit Connector Eclipse Project
=====================

Sample Informatica Cloud Connector SDK connector with read and write capability.

Pre-Requisites
=======

Install JDK 1.7 (32 bit)
------------------------
1. Uninstall JDK 1.6 if it’s installed. ( Informatica agent will only run with 32 bit (x86) 1.7.x JVM)
2. Download JDK. Choose Java SE Development Kit 7 Update 25 (Windows x86) or higher: http://www.oracle.com/technetwork/java/javase/downloads/index.html
3. Add the JDK bin directory to your PATH and also set JAVA_HOME environment variable

Install Apache ANT
------------------
1. Download ANT: http://apache.mirrors.pair.com//ANT/binaries/apache-ANT-1.8.4-bin.zip
2. Add ANT_HOME variable and %ANT_HOME%\bin to your Path environment variable


Install Secure Agent from the Developer Instance
------------------------------------------------
1. Uninstall existing agent first if you have already installed the agent from production
2. Refer to Getting started with connector toolkit guide for details on agent installation: https://community.informatica.com/docs/DOC-2662

Install 32 bit Eclipse IDE for Java EE Developers
--------------------------------------
1. Grab whatever the latest version is
2. From the Eclipse menu choose Windows > Preferences
3. In the preferences dialogue navigate to Java > Build Path > User Libraries
4. Click on New and enter rDTM for User Library Name and click OK
5. Choose rDTM from the library list and click on Add JARs
6. In the file dialogue navigate to <Informatica Cloud Secure Agent>\main\bin\rdtm\javalib folder
7. Select all jar files in javalib folder and click Open

Import the sample project into your workspace
----------------------------------------------
1. In Eclipse click on File >Import
2. Choose Import Existing Projects Into Eclipse
3. Choose the "Select Root directory" option and select your folder where the sample connector project is using the Browse button
4. Click Finish

Run the JUnit Tests
---------------
1. Run the INFA_JUnit test suite to verify project was imported correctly.
2. See the readme.txt for more information on the Junit tests



