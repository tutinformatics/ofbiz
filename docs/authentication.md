Authentication REST endpoint is at `:8443/api/auth/`

From there you can register a new user with given input:

POST `:8443/api/auth/v1/register`

```
{"userLoginId":"insert_username_here", "currentPassword":"insert_password_here", "currentPasswordVerify":"insert_password_here"}
```

Where currentPasswordVerify can be left empty (`null`)

Reply for that request will be either:

```
{
    "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJ1c2VyTG9naW5JZCI6Imluc2VydF91c2VybmFtZV9oZXJlIiwiaXNzIjoiQXBhY2hlT0ZCaXoiLCJleHAiOjE1ODc4NDEzMzgsImlhdCI6MTU4NzgzOTUzOH0.Gw3tafcMOaSq-7jj2Tgc_RnvMlc6hGMmDKU9xdC6wo00Lud_BBFVZEaACXn2gI4rrIZEzZD85yUDrKW-69CKIA",
    "userLoginId": "insert_username_here"
}
```


From there you can log in with given input:

POST `:8443/api/auth/v1/login`

```
{"userLoginId":"insert_username_here", "currentPassword":"insert_password_here", "currentPasswordVerify":"insert_password_here"}
```

Where currentPasswordVerify can be left empty (`null`)

Reply for that request will be either:

```
{
    "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJ1c2VyTG9naW5JZCI6Imluc2VydF91c2VybmFtZV9oZXJlIiwiaXNzIjoiQXBhY2hlT0ZCaXoiLCJleHAiOjE1ODc4NDEzMzgsImlhdCI6MTU4NzgzOTUzOH0.Gw3tafcMOaSq-7jj2Tgc_RnvMlc6hGMmDKU9xdC6wo00Lud_BBFVZEaACXn2gI4rrIZEzZD85yUDrKW-69CKIA",
    "userLoginId": "insert_username_here"
}
```

The given token must be present in Header as follows:

Authorization: Bearer <token>

With that Ofbiz services and entities can be used