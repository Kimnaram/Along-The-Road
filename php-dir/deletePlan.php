<?php
error_reporting(E_ALL);
ini_set('display_errors',1);

include('dbcon.php');

//POST 값을 읽어온다.
$uid=isset($_POST['uid']) ? $_POST['uid'] : '';
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


if ($uid != ""){

  $sql="delete from plan where uid='$uid'";
  $stmt = $con->prepare($sql);
  $stmt->execute();

  $selectsql="select * from plan where uid='$uid'";
  $stmt2 = $con->prepare($selectsql);
  $stmt2->execute();

  if ($stmt2->rowCount() == 0){

        $result = "삭제 성공";
	echo $result;

  }
	else{

   		$result = array();

        while($row=$stmt->fetch(PDO::FETCH_ASSOC)){

        	extract($row);

            array_push($result,
                array("uid"=>$row["uid"],
                "city"=>$row["city"],
                "start_date"=>$row["start_date"],
                "end_date"=>$row["end_date"]
            ));
        }


        if (!$android) {
          $json = json_encode(array("result"=>$result), JSON_UNESCAPED_UNICODE);
          echo $json;
        }else
        {
            header('Content-Type: application/json; charset=utf8');
            $json = json_encode(array("result"=>$result), JSON_UNESCAPED_UNICODE);
            echo $json;
        }
    }
}
else {
    echo "Plan : ";
}

?>



<?php

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if (!$android){
?>

<html>
   <body>

      <form action="<?php $_PHP_SELF ?>" method="POST">
	UID: <input type = "text" name = "uid" />
        <input type = "submit" />
      </form>

   </body>
</html>
<?php
}


?>
