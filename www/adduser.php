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
          <li role="presentation" class="active"><a href="adduser.php">New User</a></li>
          <li role="presentation"><a href="blocklist.php">Block List</a></li>
      </ul>
      </div>
    </div>
</nav>

<div class="container-fluid center_div">

      <form action="useradded.php" method="POST" class="form-horizontal">
      <div>
          <h4 class="text-center" style="margin-top: 40px;">Add new user</h4>
        </div>
        <div class="form-group">
          <label for="id" class="col-sm-2 control-label col-sm-offset-2">Username: </label>
          <div class="col-sm-4">
            <input type="text" class="form-control" id="id" value="" name="id" >
          </div>
        </div>
      <div class="form-group">
          <label for="password" class="col-sm-2 control-label col-sm-offset-2">Password: </label>
          <div class="col-sm-4">
            <input type="password" class="form-control" id="password" value="" name="password" >
          </div>
        </div>
        <button name="add" type="submit" class="btn btn-primary col-sm-2 col-sm-offset-5" onclick="al()">Submit</button>
      </form>
    </div>
  </div>

</body>
</html>