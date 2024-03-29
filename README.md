# HealthGuardian

***
<div align="center">
 <b>HealthGuardian: Smart Health Monitoring</b>
 <br>
</div> 

***
#

**Java-based**, **multithreaded**, **TCP-based** and **clinet-server** `health application` project to monitor and analyze the user's health condition with a mechanism for assessing the need for a doctor's consultation and the ability to store and share health data history. 

> [!IMPORTANT]
> While creating the application, an interview was conducted with a ***dr Piotr Pastwa*** (Internist, Pulmonologist). The interview was needed to obtain information about what is most important for the Doctor in such applications. In addition, thanks to this, we were able to professionally prepare the Short Medical Interview panel.

## Folder Structure

The `main` workspace contains two folders by default, where:
* `java`: the folder to maintain core backend of application
  
   - `Client`: the folder to maintain sources of client

   -  `Server`: the folder to maintain sources of server

   -  `ScenesControllers`: the folder to maintain scenes dependencies

   - `utils` the folder to maintain utils classes

*  `resources`: the folder to maintain layout and view of application

   - `ScenesLayout`: the folder to maintain layout of scenes in fxml type

   - `Images`: the folder to maintain photos/backgrounds

   - `Styles`: the folder to maintain CSS files

The `test` workspace contains unit, integration and end-to-end tests.

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

## Starting Server
Firstly you have to run a `Server` class.
 
## Starting Client
Client will only work if server is started.
To start client you have to run `Client` class

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).
