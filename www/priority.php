<?php
$valid_passwords = array ("admin" => "password");
$valid_users = array_keys($valid_passwords);

$user = $_SERVER['PHP_AUTH_USER'];
$pass = $_SERVER['PHP_AUTH_PW'];

$validated = (in_array($user, $valid_users)) && ($pass == $valid_passwords[$user]);

if (!$validated) {
  header('WWW-Authenticate: Basic realm="My Realm"');
  header('HTTP/1.0 401 Unauthorized');
  die ("Not authorized");
}

?>

<html>
  <head>
    <title>Proxy Configuration</title>

    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="viewport" content="initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    <link rel="stylesheet" href="bootstrap.min.css">
    <!-- Optional theme -->
    <link rel="stylesheet" href="bootstrap-theme.min.css">
    <script src="jquery.min.js"></script>
    <script src="bootstrap.min.js"></script>
    <script src="sdk.js"></script>
    <style type="text/css">
      .center_div{
        margin: 0 auto;
        min-width: 40%;
      }
      footer{
        margin-top: 10px;
      }.links li{
        border: 1px solid #52b3d9;
        border-radius:2px;
        color: #212121;
      }
      .links li a:hover{
        background-color: #3498db;
        color: #fefefe;
      }
      html, body{
        height: 100%;
      }
      body{
        background: linear-gradient(rgba(129, 207, 204, 0.5),rgba(197,239,247, 0.5)); 
      }
    </style>
  </head>
  <body>

<nav class="navbar navbar-default" role="navigation">
    <div class="container-fluid">
      <div class="navbar-header">
          <ul class="nav nav-pills" style="margin-top: 4px;">
          <li role="presentation"><a href="adduser.php">New User</a></li>
          <li role="presentation"><a href="blocklist.php">Block List</a></li>
          <li role="presentation" class="active"><a href="priority.php">Set Priorities</a></li>
      </ul>
      </div>
    </div>
</nav>
<div class="container-fluid center_div">    

      <form action="success.php" method="POST" class="form-horizontal">
      <div>
          <h4 class="text-center" style="margin-top: 40px;">Select Components</h4>
        </div>
        
  <?php
$servername = "localhost";
$username = "root";
$password = "Nahiyaad";
$dbname = "proxy";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 

$sql = "SELECT id FROM login";
$result = $conn->query($sql);

if ($result->num_rows > 0) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
        echo  "<div class=\"form-group\"><label for=\"c1\" class=\"col-sm-2 control-label col-sm-offset-2\">
          " . $row["id"] . "</label><div class=\"col-sm-4\"><input type=\"number\" width=3 min=1 max=10 class=\"form-control\" id=\"c1\" name=\"compression\"></div></div>";
    }
} else {
    echo "0 results";
}
$conn->close();
?>
      
        <button type="submit" class="btn btn-primary col-sm-2 col-sm-offset-5" onclick="al()">Apply Changes</button>

      </form>
    </div>
  </div>
</body>
</html>
