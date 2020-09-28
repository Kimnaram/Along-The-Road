<?php
error_reporting(E_ALL);
ini_set('display_errors',1);

include('dbcon.php');

//POST 값을 읽어온다.
$postId=isset($_POST['postId']) ? $_POST['postId'] : '';
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if ($postId != ""){

  $sql="select * from reviews where postId=$postId";
  $stmt = $con->prepare($sql);
  $stmt->execute();

  if ($stmt->rowCount() == 0){

        echo "";
        echo $postId;
        echo "은 찾을 수 없습니다.";
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
			"uid"=>$row["uid"],
			"image"=>$row["image"]
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
