# [Telespazio Challenge](https://github.com/misrraimsp/telespazio)

### How To Use
1. [Install JDK 14](https://docs.oracle.com/en/java/javase/14/install/index.html) on your machine.

2. Unzip the provided folder.

3. Assuming the unzipped folder address is `uzpath`, the pass-schedule file is in `schedulePath`, and the ground base bandwidth is `maxAllowedRate`, type in the terminal: 
`java -cp uzpath/telespazio/out/production/telespazio/ misrraimsp.Telespazio schedulePath maxAllowedRate`

4. The program will generate as output a file with the requested information, named `out.txt`, in the same location of the pass-schedule file.

### Implementation Notes

- In the sample file all times coincide in hours at o'clock and at half past. But it is only specified that the minimum window is half an hour, not that it has to be some multiple of 30 minutes. Accuracy in time measurements is not specified in the documentation, so it is assumed to be 1 minute.

- From the problem domain it is assumed that the communication time (*X*) and the non-communication time (*Y*) are cyclical: *X* minutes communicating, followed by *Y* minutes without communicating, and again starting with *X* minutes communicating and so on (*X*,*Y*,*X*,*Y*,*X*,*Y* ...)

- The *X*+*Y* sum is assumed to be 24 hours (=1440 minutes). This assumption is based on the information available in the sample file, as it contains temporary data in *hh:mm* format. If the cycle *X*+*Y* were greater than 24h, it would be necessary to specify the passage from one day to another.

- If the start time coincides with the end time, it is interpreted as that the satellite is geostationary and that it is constantly transmitting to the base. According with the previous nomenclature, it would be the case of (*X*=1440, *Y*=0).

- If a satellite never transmit, that is (*X*=0, *Y*=1440), it is assumed that satellite would not be in the input file.

- The available time is modeled as a vector with 1440 values, in which index 0 corresponds to the time point 00:00, and index 1439 corresponds to 23:59.

- **The concept of total downlink is understood as the total data downloaded during a 30 minutes window**. It would be equivalent to calculating the 30 minutes integral of the aggregate rate curve.

- There is not necessarily uniqueness in the 30 minutes window with maximum data downloading. Several such windows can be found. For instance, if there were only a single geostationary satellite in the input file, it would produce (1440-29)=1411 max load windows. Therefore, the developed solution returns a vector with all the maximum load periods found.

- The program creates a file, called out.txt, in which it dumps the requested information.

- The structure of the output file is as follows:
	- In the first line, both the available ground station bandwidth and the maximum aggregate downlink rate are written. If the first quantity is greater than or equal to the second then "OK" is written, and otherwise "EXCEEDS" is written. For example **OK {maxAllowedRate: 45, currentMaxRate: 16}**
	- The second line contains the number (*N*) of found max load windows.
	- The following *N* lines contain, in **hh:mm,hh:mm** format, the max load window start and end time points.
	
- As an improvement, it would be interesting that once the maximum load windows have been obtained, those that are contiguous are accumulated, and that what is written in the output file are the accumulated periods of maximum load. For example, instead of writing as 4 different windows **20:00,20:29**, **20:01,20:30**, **20:02,20:31** and **20:03,20:32**, only type the window **20:00,20:32**. However, as the specification expects 30 minute windows, this accumulation has not been implemented.


