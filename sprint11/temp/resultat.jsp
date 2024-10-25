<!DOCTYPE html>
<html>
<head>
    <title>Submit Form</title>
</head>
<body>
    <form action="model" method="Post">
        <div>
            <label for="firstName">Enter your first name:</label>
            <input type="text" id="name" name="emp.name" required>
        </div>
        <div>
            <label for="firstName">Enter your first name:</label>
            <input type="text" id="firstName" name="nom" required>
        </div>
        <div>
            <label for="lastName">Entrer votre age:</label>
            <input type="number" id="lastName" name="age" required>
        </div>
        <div>
            <button type="submit">Submit</button>
        </div>
    </form>
</body>
</html>

<%
    String valiny="Tsy tonga";
    if(request.getAttribute("nom")!=null)
    {
        valiny=(String)request.getAttribute("nom");
    }
    out.println(valiny);
%>