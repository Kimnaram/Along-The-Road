<?php

	$host = '3.92.65.36';
	$username = 'naram';
	$password = 'kimnaram';
	$dbname = 'alongtheroad';

	$con = mysqli_connect($host, $username, $password, $dbname, '3306');

	if(mysqli_connect_errno())  {
		echo "Failed to connect to MySQL : " . mysqli_connect_error();
	}

	$sql = "SELECT VERSION()";
	$result = mysqli_query($con, $sql);
	$row = mysqli_fetch_array($result);
	print_r($row["VERSION()"]);

?>
