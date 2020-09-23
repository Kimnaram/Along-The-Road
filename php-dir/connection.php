<?php

$host = '3.92.65.36';
$username = 'naram'; # MySQL 계정 아이디
$password = 'kimnaram'; # MySQL 계정 패스워드
$dbname = 'alongtheroad';  # DATABASE 이름

$con = mysqli_connect($host, $username, $password, $dbname);

if (mysqli_connect_errno($con)) {
  echo "Failed to connect to MySQL : " . mysqli_connect_error();
}

mysqli_set_charset($con, "utf8");

$res = mysqli_query($con, "select * from reviews");

$result = array();

while($row = mysqli_fetch_array($res)) {
  array_push($result,
    array("postId"=>$row[0],"title"=>$row[1],"content"=>$row[2],"name"=>$row[3],"uid"=>$row[4]));
}

echo json_encode(array("result"=>$result), JSON_UNESCAPED_UNICODE);

mysqli_close($con);

?>

