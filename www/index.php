<?php
require_once("include/config.php");
require_once("connections/august.php");
include("include/$topfile");

$stars = mysql_query(
    "SELECT username, birth, death, velocity_magnitude, accel_magnitude, mass, radius 
        FROM object NATURAL JOIN star 
        LEFT JOIN (user) ON (object.owner = user.id)", $august);
echo "<div><table id=\"star_table\">";
echo "<thead>";
echo "<tr>
        <th scope=\"col\" id=\"place_col\">Birth Place</th>
        <th scope=\"col\" id=\"birth_col\">Birthday</th>
        <th scope=\"col\" id=\"death_col\">Collapse Day</th>
        <th scope=\"col\" id=\"velocity_col\">Velocity</th>
        <th scope=\"col\" id=\"acceleration_col\">Acceleration</th>
        <th scope=\"col\" id=\"mass_col\">Mass</th>
        <th scope=\"col\" id=\"radius_col\">Radius</th>
     </tr>";
echo "</thead><tbody>";
while ($star = mysql_fetch_row($stars)) {
    echo "<tr>";
    for ($i = 0; $i < count($star); $i++){
        echo "<td>";
        echo $star[$i];
        echo "</td>";
    }
    echo "</tr>";
}
echo "</tbody></table>";
?>
