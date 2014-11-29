<?php
$dbhost = 'localhost';
$dbuser = 'root';
$dbpass = 'Nahiyaad';
$conn = mysql_connect($dbhost, $dbuser, $dbpass);
if(! $conn )
{
  die('Could not connect: ' . mysql_error());
}
// echo 'Here1';

if(! get_magic_quotes_gpc() )
{
   $emp_name = addslashes ($_POST['id']);
   $emp_address = addslashes ($_POST['password']);
}
else
{
   $emp_name = $_POST['id'];
   $emp_address = $_POST['password'];
}
// echo $_POST['id'];

$sql = "INSERT INTO login ".
       "VALUES('$emp_name','$emp_address')";
mysql_select_db('proxy');
$retval = mysql_query( $sql, $conn );
if(! $retval )
{
	echo "<h2>Could not enter data: </h2>" . mysql_error();
  // die('Could not enter data: ' . mysql_error());
}
else{
echo "<h2> Please restart the proxy server for changes to take effect. </h2>";
}
mysql_close($conn);

?>