<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="it.soundmate.controller.logic.MessagesController" %>
<%@ page import="java.util.List" %>
<%@ page import="it.soundmate.controller.logic.SearchController" %>
<%@ page import="it.soundmate.controller.logic.NotificationsController" %>
<%@ page import="it.soundmate.model.*" %>
<%@ page import="java.util.ArrayList" %><%--
  ~ Copyright (c) 2021.
  ~ Created by Lorenzo Pantano on 07/02/21, 22:07
  ~ Last edited: 07/02/21, 22:07
  --%>
<%--
  Created by IntelliJ IDEA.
  User: lpant
  Date: 07/02/2021
  Time: 22:07
  To change this template use File | Settings | File Templates.
--%>
<jsp:useBean id="messageBean" class="it.soundmate.model.Message"/>
<jsp:setProperty name="messageBean" property="body"/>
<%@ page contentType="text/html;charset=UTF-8"%>
<!DOCTYPE>
<html lang="it">
<head>
    <title>Messages</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.0/dist/css/bootstrap.min.css" integrity="sha384-B0vP5xmATw1+K9KRQjQERJvTumQW0nPEzvF6L/Z6nronJ3oUOFUFpCjEUQouq2+l" crossorigin="anonymous">
    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js" integrity="sha384-DfXdz2htPH0lsSSs5nCTpuj/zy4C+OGpamoFVy38MVBnE+IbbVYUew+OrCXaRkfj" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.0/dist/js/bootstrap.bundle.min.js" integrity="sha384-Piv4xVNRyMGpqkS2by6br4gNJ7DXjqk09RmUpJ8jgGtD7zP9yug3goQfGII0yAns" crossorigin="anonymous"></script>
</head>
<body>

    <%
        User loggedUser = (User) session.getAttribute("loggedUser");
        MessagesController messagesController = new MessagesController();
        NotificationsController notificationsController = new NotificationsController();
        List<Message> messageList = messagesController.getMessagesForUser(loggedUser);
        List<Notification> notificationList = notificationsController.getMessagesForUser(loggedUser);
        List<BookingNotification> bookingNotificationsList = new ArrayList<>();
        List<JoinRequestNotification> joinRequestNotificationList = new ArrayList<>();
        for (Notification notification: notificationList) {
            switch (notification.getMessageType()) {
                case JOIN_BAND_CONFIRMATION:
                case JOIN_BAND_CANCELED:
                    JoinRequestNotification joinRequestNotification = (JoinRequestNotification) notification;
                    joinRequestNotificationList.add(joinRequestNotification);
                    break;
                case BOOK_ROOM_CONFIRMATION:
                case BOOK_ROOM_CANCELED:
                    BookingNotification bookingNotification = (BookingNotification) notification;
                    bookingNotificationsList.add(bookingNotification);
                    break;
            }
        }
        request.setAttribute("messageList", messageList);
        request.setAttribute("notificationsList", notificationList);
        request.setAttribute("bookingNotifications",bookingNotificationsList);
        request.setAttribute("joinRequestNotifications", joinRequestNotificationList);
        for (Message message : messageList) {
            if (request.getParameter("reply" + message.getMessageCode()) != null) {
                messageBean.setIdReceiver(message.getIdSender());
                messageBean.setIdSender(message.getIdReceiver());
                messageBean.setSubject(message.getSubject());
                messageBean.setSenderUserType(loggedUser.getUserType());
                messagesController.sendMessage(messageBean);
                System.out.println("Message has been sent to: "+messageBean.getIdReceiver() + " "+messageBean.getBody());
            }
        }

    %>

    <!-- Navigation -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <a class="navbar-brand" href="#">Soundmate</a>

        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav mr-auto">
                <li class="nav-item">
                    <a class="nav-link" href="home.jsp">Home</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link active" href="messages.jsp">Messages<span class="sr-only">(current)</span></a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="profile.jsp">Profile</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="index.jsp">Logout</a>
                </li>
            </ul>
            <form class="form-inline my-2 my-lg-0">
                <input class="form-control mr-sm-2" type="search" placeholder="Search" aria-label="Search" name="searchString">
                <button class="btn btn-outline-success my-2 my-sm-0" type="submit" name="search">Search</button>
            </form>
        </div>
    </nav>

    <h2>Messages and Notifications for <%=loggedUser.getName()%></h2>

    <div id="exTab2" class="container">
        <ul class="nav nav-tabs">
            <li class="active">
                <a  href="#1" data-toggle="tab">Messages</a>
            </li>
            <li><a href="#2" data-toggle="tab">Bookings Notifications</a>
            </li>
            <li><a href="#3" data-toggle="tab">Join Request Notifications</a>
            </li>
        </ul>

        <div class="tab-content ">
            <div class="tab-pane active" id="1">
                <!--Messages-->
                <c:forEach items="${messageList}" var="message">
                    <div class="d-flex justify-content-around">
                        <h3><c:out value="${message.sender.name}"/></h3>
                        <p><c:out value="${message.subject}"/></p>
                        <a class="btn btn-primary" data-toggle="collapse" href="#collapseExample${message.messageCode}" role="button" aria-expanded="false" aria-controls="collapseExample">
                            Read
                        </a>
                    </div>
                    <div class="collapse" id="collapseExample${message.messageCode}">
                        <div class="card card-body">
                            <p><c:out value="${message.body}"/></p>
                            <form>
                                <div class="form-group">
                                    <label for="exampleFormControlTextarea1">Reply</label>
                                    <textarea class="form-control" id="exampleFormControlTextarea1" rows="3" name="body"></textarea>
                                </div>
                                <input type="submit" class="btn btn-primary" value="Reply" name="reply${message.messageCode}"/>>
                            </form>
                        </div>
                    </div>
                </c:forEach>
            </div>
            <div class="tab-pane" id="2">
                <!--Booking Notifications-->
                <c:forEach items="${bookingNotifications}" var="notification">
                    <div class="d-flex justify-content-around">
                        <h3><c:out value="${notification.booking.booker.name}"/></h3>
                    </div>
                    <div class="collapse" id="collapseExample${notification.messageId}">
                        <div class="card card-body">
                            <p><c:out value="${notification.messageType}"/></p>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>

    <h1>Messages for </h1>




</body>
</html>
