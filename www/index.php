<?php
require_once("include/config.php");
require_once("connections/august.php");
include("include/$topfile");

$stars = mysql_query(
    "SELECT username, birth, death, mass, radius, luminosity, frequency, state.name
        FROM object NATURAL JOIN star 
        LEFT JOIN (user) ON (object.owner = user.id)
        LEFT JOIN (state ON (object.state = state.id)
        ORDER BY birth DESC", $august);
echo "<div><table id=\"star_table\">";
echo "<thead>";
echo "<tr>
        <th scope=\"col\" id=\"place_col\">Birth Place</th>
        <th scope=\"col\" id=\"birth_col\">Birthday</th>
        <th scope=\"col\" id=\"death_col\">Collapse Day</th>
        <th scope=\"col\" id=\"mass_col\">Mass</th>
        <th scope=\"col\" id=\"radius_col\">Radius</th>
        <th scope=\"col\" id=\"luminosity_col\">Luminosity</th>
        <th scope=\"col\" id=\"frequency_col\">Frequency</th>
        <th scope=\"col\" id=\"state_col\">State</th>
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
