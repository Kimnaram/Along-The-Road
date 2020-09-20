<?php
error_reporting(E_ALL);
ini_set('display_errors',1);

include('dbcon.php');

//POST 값을 읽어온다.
$postId=isset($_POST['postId']) ? $_POST['postId'] : '';
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


if ($postId != ""){

  $sql="delete from reviews where postId=$postId";
  $stmt = $con->prepare($sql);
  $stmt->execute();

  $selectsql="select * from reviews where postId=$postId";
  $stmt2 = $con->prepare($selectsql);
  $stmt2->execute();

  if ($stmt2->rowCount() == 0){

        $result = "삭제 성공";
        echo $result;

        $altersql="alter table reviews auto_increment=1";
        $stmt = $con->prepare($altersql);
        $stmt->execute();

        $setsql="set @count = 0";
        $stmt = $con->prepare($setsql);
        $stmt->execute();

        $updatesql="update reviews set postId = @count:=@count+1";
        $stmt = $con->prepare($updatesql);
        $stmt->execute();
  }
	else{

   		$result = array();

        while($row=$stmt->fetch(PDO::FETCH_ASSOC)){

        	extract($row);

            array_push($result,
                array("postId"=>$row["postId"],
                "title"=>$row["title"],
                "content"=>$row["content"],
                "name"=>$row["name"],
                "uid"=>$row["uid"]
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
    echo "Reivew : ";
}

?>



<?php

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if (!$android){
?>

<html>
   <body>

      <form action="<?php $_PHP_SELF ?>" method="POST">
         postId: <input type = "text" name = "postId" />
         <input type = "submit" />
      </form>

   </body>
</html>
<?php
}


?>
