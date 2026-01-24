# CineVerse
### Spring project with main focus on server logic
<hr>

### Implemented functionalities:
#### 1. Register with email, password, public and private name. Private name is used for authentication. Only public name is visible to other users. <br> 2. Login with either email or private name. Optional add 2fa login using totp ( 2fa app for mobile ). <br> 3. Log out of account that also clears jwt token locally. <br> 4. Edit profile page - edit public name, bio, profile picture with preview. <br> 5. Chat page - search and add friend by their public name and start chatting with them ( no Netty SocketIO, just http requests - i have implemented socket protocol in several other projects ) <br> 6. See top 10 users page - see latest updated profiles. <br> 7. Upload files page - upload/remove up to 5 files, max 5MB each. <br> 8. Home page - has links to all other pages and checkbox to enable/disable 2fa ( requires QR code scan and 2fa code confirmation when enabled ). <br> 9. Admin paqge - only users with admin role are allowed. Search and browse users ( page paggination included ). <br> 10. 404 page - awesome 404 page that kindly redirects user automatically to home page ( can redirect manually ). <br> 11. 401 page - awesome 401 page for people who try to access admin page without being authorized ( same auto and manual redirect as 404 page). <br> 12. 429 page - easy there, speedrunner. You send too many requests <br> 13. Secrete page - there is a hidden page with a cute cat animation

### PostgreSQL database:
#### User table - id, bio, email, password, profile_picture_url, public_name, username, updated_at, totp_secrete and totp_enabled
#### Roles table - id and role name
#### User roles table - user id and role id
#### User files table
<img width="1080" height="266" alt="Screenshot (357)" src="https://github.com/user-attachments/assets/e64dbb27-8ce8-43f0-b18d-25973b60d33f" />

#### Audit log table
<img width="935" height="253" alt="Screenshot (355)" src="https://github.com/user-attachments/assets/00be2ad0-bf66-45c4-9dab-aa52d2cea440" />
