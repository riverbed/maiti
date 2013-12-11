# Mobile Application Instrumentation Telemetry Interface (MAITI) 

The Mobile Application Instrumentation Telemetry Interface (MAITI) provides Android and iOS APIs to monitor mobile application performance and usage.

Developers use the APIs to "instrument" their applications and report data to BrowserMetrix, Riverbed’s MAITI-compliant collection server.  The BrowserMetrix Web user interface presents detailed insight into end-to-end performance, geography, platform type, and response time. In addition to mobile app data, BrowserMetrix also collects web page performance and usage data via JavaScript page tagging. BrowserMetrix can be deployed as a SaaS or On-premise solution.

Developers add calls to MAITI in their apps that enable transaction monitoring.  (In this context, a "transaction" is any application activity for which the developer wants to measure elapsed time.)

MAITI uses transaction bracketing to detect the start and end of user transactions and records the response time and other metrics. For instance, such an interval transaction could begin with the user tapping a button, and end with the resulting display of data. MAITI allows developers to capture response times for an unrestricted number of transactions. 

Within transactions, developers can specify events and free-form user data to associate with an interval transaction.  These elements all appear in the BrowserMetrix user interface as transaction details.  Events denote any activity of interest within a transaction.  User "tags" are searchable in the BrowserMetrix user interface.  They can record any contextual information the developer wants, such as the user name, or the name and version of the mobile app. Another user data element is not searchable but can contain more data, so is suitable for stack traces and longer text.

Developers can also specify "notification" transactions that appear in the BrowserMetrix user interface as user-searchable transactions.  Unlike interval transactions, notifications do not have a start and end time.  Like events, notifications denote an activity of interest but are not associated with an interval transaction. 

This document provides some simple examples to get started and reference information about the APIs.  Complete sample apps are available with the MAITI source code.

## Examples

The following examples provide a basic introduction to instrumenting your app with MAITI.


### Android Example

Instantiate the **UserExperience** class as a singleton object.  One **UserExperience** object can be used throughout your Android app.  Typically, you extend the **Application** class and initialize the **UserExperience** object in the **onCreate method**.  Remember to declare your Application subclass in the *AndroidManifest.xml* file.

The following code snippet initializes the MAITI library as the **SampleApplication** application subclass and creates the ue object instance of **UserExperience**.

``` java
public class SampleApplication extends Application {

	private UserExperience ue = null;
	
	@Override
	public void onCreate() {
		try
		{
			ue = new UserExperience( "[YourCustomerID]", "[YourAppId]", this.getApplicationContext());
		} catch (PermissionsException) {
			throw new RuntimeException("Required Permissions are not declared in the AndroidManifest.xml");
	}

		super.onCreate();	
	}

	public UserExperience getAppPerformanceMonitor()
	{
		return ue;
	}
}}

```


Next, use the **ue** object to measure and report performance.  This example measures transaction performance for a method called **showList** inside of an activity:

``` java
private void showList()
{
	UserExperience eue = ((SampleApplication) getApplication()).getAppPerformanceMonitor();

	TransactionId transId = eue.transactionStart("Show Music List");

	downloadMusic();
	updateList();
	renderList();

	eue.transactionEnd(transId);
}

```


### iOS Example

Instantiate the **PerformanceLibrary** object once and use it throughout the iOS app.  Initialize **PerformanceLibrary** in the **applicationDidFinishLaunching** function in your application delegate, as shown below:

``` objective-c
#import "PerformanceLibrary.h"

@synthesize _PerformanceLibrary;

- (void)applicationDidFinishLaunching:(UIApplication *)application
{
    _PerformanceLibrary=[[PerformanceLibrary alloc] 
			initWithCustomerId:@"[YourCustomerID]" appId:@"[YourAppID]"];

}

```

Once initialized, use the **PerformanceLibrary** object to measure transaction performance.  The example below measures the duration of the **showList** function inside of a UIViewController:

``` objective-c
- (void)showList
{
	NSString *transId = 	[[_AppDelegate _PerformanceLibrary] 
				TransactionStart: @"Show Music List"];
	
	[self downloadMusic]; 
	[self updateList];
	[self renderList];

	[[_AppDelegate _PerformanceLibrary] TransactionEnd:transId];
}

```


## Android API

### Installation

Download the MAITI source code and add it to Eclipse as anAndroid project.

* File->Import->General->Existing Projects into Workspace
    
Add the MAITI project to your Android code as an Android library:

* Right click on your Android app project and select Properties
* Select the "Android" item on the left window pane
* Add a new library project and select the MAITI project


### UserExperience Class

Provides methods for specifying interval transactions, events, notification transactions, and user data.


#### Constructor

Creates a UserExperience object. Instantiate a UserExperience object only once, during initialization of the application. 

``` java
public UserExperience(String customerId, String appId, Context caller_context) throws PermissionsException

public UserExperience(String customerId, String appId, Context caller_context, SettingsObject settings) throws PermissionsException
```

**customerId**: The unique Customer ID for your organization.  This value is generated when configuring the application in the BrowserMetrix Web user interface.

**appId**: The unique Application ID for this application.  This value is generated when configuring the application in the BrowserMetrix Web user interface.

**caller_context**: Calling activity or application context

**settings**: An optional SettingsObject object that defines custom settings for this UserExperience object.


#### Exceptions

PermissionsException is thrown if required permissions are not found in manifest file; Depending on the options that you specified, some permissions may be required.

Permission | Required
-----------|---------
permission.INTERNET | Always required
permission.ACCESS_NETWORK_STATE | Required by default unless the option to record connection information is disabled in the **SettingsObject** object
permission. READ_PHONE_STATE | Required by default unless the option to record the serial number is disabled in the **SettingsObject** object



#### Methods

##### Set Enabled

Specifies whether or not to enable the performance monitoring library.  This method allows applications to disable the library at runtime.  (Use the setEnabled method of the SettingsObject class (page 13) to enable or disable the library when it is loaded.)

``` java
public void setEnabled(boolean enabled)
```


**enabled**: Default value of TRUE means the library is active.

**Return Value**: None




##### transactionStart

Starts an interval transaction and returns a TransactionId object used in other methods to refer to the transaction.

Interval transactions can optionally specify a parent transaction.  The BrowserMetrix Web user interface indicates child and parent transactions.  For example, a transaction named "Login" could contain two child transactions named "Authorize Credentials" and "Gather User Info".

``` java
public TransactionId transactionStart(String transactionName)

public TransactionId transactionStart(String transactionName, TransactionId parentTransactionId)
```


**transactionName**: Name that identifies the transaction in the BrowserMetrix Web user interface.

**parentTransactionId**: ID of the parent transaction. 

**Return Value**: ID of the interval transaction.  Use this ID in other methods (to end the transaction and to add data or events in it).



##### setTransactionEvent 

Specifies an event within an interval transaction.  Events appear in the BrowserMetrix Web user interface along with the time they occurred.

``` java
public void setTransactionEvent(String eventName, TransactionId transactionId)
```

**eventName**: Name of the event that appears in the BrowserMetrix Web user interface.

**transactionId**: ID of the transaction to which this event is related.

**Return Value**: None


##### notification 

Specifies a notification transaction.  Notifications appear as transactions in the BrowserMetrix Web user interface and are indexed so BrowserMetrix users can search for them.

``` java
public void notification(String transactionName, String userTag)
```

**transactionName**: Name of the notification transaction that appears in the BrowserMetrix Web user interface.

**userTag**: A string that users can search for in the BrowserMetrix user interface.

**Return Value**: None


##### setTransactionUserData  

Associates a string of up to 16,000 characters with the transaction.  This data can be anything that may help with diagnosing errors or slow performance, such as stack traces, user profile data, or response codes.  Unlike the setTransactionUserTag methods, users cannot search for this string in the BrowserMetrix user interface.  The method truncates the string at 16,000 characters.

``` java
public void setTransactionUserData(TransactionId transactionId, String userData)
```

**transactionId**: ID of the associated transaction.

**userData**: Text to associate with this transaction.

**Return Value**: None


#### setTransactionUserTag1-3

Specifies a string associated with a transaction that users can search for in the BrowserMetrix console.  This string can be anything that may help find interesting transactions.  There are three searchable tags that can be used.  This string value is truncated at 128 characters.

``` java
public void setTransactionUserTag1(TransactionId transactionId, String userData)
public void setTransactionUserTag2(TransactionId transactionId, String userData)
public void setTransactionUserTag3(TransactionId transactionId, String userData)
```

**transactionId**: ID of the associated transaction.

**userData**: User-searchable string that identifies the transaction in BrowserMetrix.

**Return Value**: None

#### setTransactionError

Specify an error message to associate with this transaction.  The error message string is searchable in the BrowserMetrix user interface and is truncated at 128 characters.

``` java
public void setTransactionError(TransactionId transactionId, String errorMessage)	
```

**transactionId**: ID of the associated transaction.

**errorMessage**: The error message for the transaction.  The string is truncated at 128 characters

**Return Value**: None


#### transactionEnd

Ends an interval transaction.

``` java
public void transactionEnd(TransactionId transactionId)	
```

**transactionId**: ID of the transaction to end.

**Return Value**: None


### SettingsObject Class

Provides methods for overriding default settings values.  Implementing this class is optional.  The UserExperience constructor  can optionally specify a SettingsObject object.


#### Constructor

Creates a **SettingsObject** object. 

``` java
public SettingsObject()
```


#### Methods


##### setEnabled

**enabled**: The default is TRUE (enabled).

**Return Value**: None


##### setDataCollector

Specifies connection details for the BrowserMetrix data collector. 


``` java
public void setDataCollector(String hostname)
public void setDataCollector(String hostname, boolean useHttps)
public void setDataCollector(String hostname, boolean useHttps, int port)
```

**hostname**: The fully-qualified domain name or IP address of the BrowserMetrix data collector.  The default value is eue.collect-opnet.com.

**useHttps**: Whether to use a Secure Sockets Layer (SSL) connection.  The default value is FALSE.

**port**: The network port to use for the connection.  The default value is 80.

**Return Value**: None


##### setRecordSerial 

Specifies whether or not to provide the serial number of the mobile device in the data sent to BrowserMetrix.


``` java
public void setRecordSerial(boolean recordSerial)
```

**recordSerial**: The default value is TRUE.  If set to FALSE, MAITI generates its own identifier.

**Return Value**: None


##### setRecordMemory 

Specifies whether or not to provide details about memory on the mobile device (total and free) in the data sent to BrowserMetrix. 

``` java
public void setRecordMemory(boolean recordMemory)
```

**recordMemory**: The default value is TRUE.

**Return Value**: None



##### setRecordConn 

Specifies whether or not to provide details about the mobile device connection (Wi-Fi or cellular) in the data sent to BrowserMetrix.

``` java
public void setRecordConn(boolean recordConn)
```

**recordConn**: The default value is TRUE.

**Return Value**: None


## iOS API

### Installation

Include the source MAITI source code in your Xcode project.

Add the SystemConfiguration.framework to your project.


### Property Settings

The project files include the *riverbed_preferences.plist* property list for the following settings that you can change:


Setting        | Meaning           | Default 
 ------------- |-------------| -----
 Enabled     | Specifies whether or not to enable the performance monitoring library.  | TRUE
 DataCollectorHost | The fully-qualified domain name or IP address of the BrowserMetrix data collector.  | mobile.collect-opnet.com
 UseHTTPS | Whether to use a Secure Sockets Layer (SSL) connection to the BrowserMetrix data collector.  | FALSE
 RecordSerial | Specifies whether or not to provide the serial number of the mobile device in the data sent to BrowserMetrix. | TRUE (if set to FALSE, MAITI generates its own identifier)
 RecordMemory | Specifies whether or not to provide details about memory on the mobile device (total and free) in the data sent to BrowserMetrix. | TRUE
 DataCollectorPort | The network port to use for the connection to the BrowserMetrix data collector.  | 80
 RecordConnectionType | Specifies whether or not to provide details about the mobile device connection (Wi-Fi or cellular) in the data sent to BrowserMetrix. | TRUE
 
 
 ### PerformanceLibrary Class
 
 Creates and returns a PerformanceLibrary object.
 
 ``` objective-c
 -(id)initWithCustomerId:(NSString*) customerId appId:(NSString*)appId;
 ```
 
**customerId**: The unique Customer ID for your organization.  This value is generated when configuring the application in the BrowserMetrix Web user interface.

**appId**: The unique Application ID for this application.  This value is generated when configuring the application in the BrowserMetrix Web user interface.

**Return Value**: The PerformanceLibrary object id

#### Methods

##### SetDisabled

Specifies whether or not to disable the performance monitoring library.  The default is FALSE. This method allows applications to disable the library at runtime.  (Use the enabled property to enable or disable the library when it is loaded.)

``` objective-c
-(void)SetDisabled:(Boolean)disabled;
```

**disabled**: A boolean value that indicates whether the library should be disabled.  The default is FALSE.

**Return Value**: None

##### Notification

Specifies a notification transaction.  Notifications appear as transactions in the BrowserMetrix Web user interface and are indexed so BrowserMetrix users can search for them.

``` objective-c
-(void)Notification:(NSString*)name userTag1:(NSString*)userTag1;
```

**name**: Name of the notification transaction that appears in the BrowserMetrix Web user interface.

**userTag1**: A string that users can search for in the BrowserMetrix user interface.  This parameter is optional and is truncated at 128 characters.	

**Return Value**: None


##### TransactionStart

Starts an interval transaction and returns a TransactionId object used in other methods to refer to the transaction.

Interval transactions can optionally specify a parent transaction.  The BrowserMetrix Web user interface indicates child and parent transactions.  For example, a transaction named "Login" could contain two child transactions named "Authorize Credentials" and "Gather User Info".

``` objective-c
-(NSString*)TransactionStart:(NSString*)name;
-(NSString*)TransactionStart:(NSString*)name parentTransactionId:(NSString*)parentTransactionId;
```

**name**:
Name that identifies the transaction in the BrowserMetrix Web user interface.

**parentTransactionId**: ID of the parent transaction.

**Return Value**: An NSString containing the transaction ID of the interval transaction.  Use this ID in other methods (to end the transaction and to add data or events in it).


##### TransactionEnd

Ends an interval transaction.

``` objective-c
-(void)TransactionEnd:(NSString*)transactionId;
```

**transactionId**: ID of the transaction to end.

**Return Value**: None


##### SetErrorMessage

Specify an error message to associate with this transaction.  The error message string is searchable in the BrowserMetrix user interface and is truncated at 128 characters.

``` objective-c
-(void)SetErrorMessage:(NSString*)errorMessage transactionId:(NSString*)transactionId;
```


**errorMessage**: The error message for the transaction.  The string is truncated at 128 characters

**transactionId**: ID of the associated transaction.

**Return Value**: None

##### SetTransactionEvent

Specifies an event within an interval transaction.  Events appear in the BrowserMetrix Web user interface along with the time they occurred.

``` objective-c
-(void)SetTransactionEvent:(NSString*)eventName transactionId:(NSString*)transactionId;
```

**eventName**: Name of the event that appears in the BrowserMetrix Web user interface.

**transactionId**: ID of the transaction to which this event is related.

**Return Value**: None

##### SetUserData

Associates a string of up to 16,000 characters with the transaction.  This data can be anything that may help with diagnosing errors or slow performance, such as stack traces, user profile data, or response codes.  Unlike the setTransactionUserTag methods, users cannot search for this string in the BrowserMetrix user interface.  The method truncates the string at 16,000 characters.

``` objective-c
-(void)SetUserData:(NSString*)data transactionId:(NSString*)transactionId;
```

**data**: Text to associate with this transaction.

**transactionId**: ID of the associated transaction.

**Return Value**: None

##### SetUserTag1-3: 

Specifies a string associated with a transaction that users can search for in the BrowserMetrix console.  This string can be anything that may help find interesting transactions.  This string value is truncated at 128 characters.

``` objective-c
-(void)SetUserTag1:(NSString*)tag transactionId:(NSString*)transactionId;
-(void)SetUserTag2:(NSString*)tag transactionId:(NSString*)transactionId;
-(void)SetUserTag3:(NSString*)tag transactionId:(NSString*)transactionId;
```

**tag**: User-searchable string that identifies the transaction in BrowserMetrix.

**transactionId**: ID of the associated transaction.

**Return Value**: None


## Additional Resources

* MAITI Overview: http://media-cms.riverbed.com/documents/Riverbed-MAITI.pdf

* BrowserMetrix is Riverbed's MAITI-compliant collection server: https://appresponse.opnet.com
