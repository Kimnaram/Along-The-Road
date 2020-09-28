<?php
error_reporting(E_ALL);
ini_set('display_errors',1);

include('dbcon.php');

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android )
{

    // 안드로이드 코드의 postParameters 변수에 적어준 이름을 가지고 값을 전달 받습니다.
    $postId = $_POST['postId'];
    $title = $_POST['title'];
    $content = $_POST['content'];
    $image = $_POST['image'];

    if(empty($postId)){
        $errMSG = "Post ID를 입력하세요.";
    }
    else if(empty($title)){
        $errMSG = "제목을 입력하세요.";
    }
    else if(empty($content)){
        $errMSG = "내용을 입력하세요.";
    }

    if(!isset($errMSG))
    {
        try{
            $stmt = $con->prepare("UPDATE reviews SET title=:title, content=:content, image=:image WHERE postId=:postId");
            $stmt->bindParam(':title', $title);
	    $stmt->bindParam(':content', $content);
	    $stmt->bindParam(':image', $image);
            $stmt->bindParam(':postId', $postId);

            if($stmt->execute())
            {
                $successMSG = "리뷰를 수정했습니다.";
            }
            else
            {
                $errMSG = "리뷰 수정 에러";
            }

        } catch(PDOException $e) {
            die("Database error: " . $e->getMessage());
        }
    }

}

?>


<?php
if (isset($errMSG)) echo $errMSG;
if (isset($successMSG)) echo $successMSG;

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if( !$android )
{
?>
<html>
   <body>

        <form action="<?php $_PHP_SELF ?>" method="POST">
            POST ID : <input type = "text" name = "postId" />
            TITLE : <input type = "text" name = "title" />
	    CONTENT : <input type = "text" name = "content" />
	    IMAGE : <input type = "text" name = "image" />
            <input type = "submit" name = "submit" />
        </form>

   </body>
</html>

<?php
}
?>

