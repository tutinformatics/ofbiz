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

If you wish to refresh the token, then you can use

POST `:8443/api/auth/v1/token` to refresh your token

Use it with following body:

```
{
    "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJ1c2VyTG9naW5JZCI6Imluc2VydF91c2VybmFtZV9oZXJlIiwiaXNzIjoiQXBhY2hlT0ZCaXoiLCJleHAiOjE1ODc4NDEzMzgsImlhdCI6MTU4NzgzOTUzOH0.Gw3tafcMOaSq-7jj2Tgc_RnvMlc6hGMmDKU9xdC6wo00Lud_BBFVZEaACXn2gI4rrIZEzZD85yUDrKW-69CKIA"
}
```

Which response will be

```
{
    "token": "eyJraWQiOiJrMSIsImFsZyI6IlJTMjU2In0.eyJpc3MiOiJBcGFjaGUgT2ZiaXoiLCJhdWQiOiJUbyB3aG9tIGl0IG1heSBjb25jZXJuIiwiZXhwIjoxNTg4OTc3NzcyLCJqdGkiOiJUd003aW8yd2VGTHU3ZWVEc0Ytam13IiwiaWF0IjoxNTg4OTc3MTcyLCJuYmYiOjE1ODg5NzcwNTIsInNlY3JldCI6ImV5SmhiR2NpT2lKQk1USTRTMWNpTENKbGJtTWlPaUpCTVRJNFEwSkRMVWhUTWpVMkluMC5jdU1iUTFyUXNVbkhMdHFzRlpwWTFVR1pwSkVoaVh3Y2xHMGc0MllXUkoteEd4NFRmTzFtNHcuUzF4djQtblFaRTBlUWYtRkNrSkVBQS44SjhSUlJHeWJ2WFpPUjlvYUpaV3ZCOHRJT3ZlSTcybVU4dG9lMzAxWklaVEFtZFgyZEtIdEgySXdNc1NuMGx1ZWhBWXRuQXo3ZU5hNUREdmZmOWhEZy5WVTRSSExfNVh0V2pjN1NVT3Z0YnNRIn0.P9T9lVuQ0Sn21VKQmEkbw4TXZNdXkUWKNj6CgOOfJVRkPRv621wEr18XcxsgTs1ZMKnyBnfr2icJFDr9bSDDCHFJIxs9G4-9Rf8hs_QQCWlj6gpHiw10_-W2M1hKZwowev9_rvn9hQ57kuYW_my9-WfV7rkNi_w3kXR8UZDE_k1E6sYF7Z7dP8k6yGEGGh2miPYp7gu7WlgmxztYCUVuaidf2r989NHKSX7WKWhr5CdOcb-il-sJx-Xq5y0P_eZVaBe0qGY4sbizZP3ckzSLTqfzRic5YCkRhSKNo-wf8VuGtFOc9Mxtek4fjb6jNdX3ryeXswIBfzmjds41fCGITg",
    "userLoginId": "admin"
}
```

Where a new token will be generated with longer duration

Token's content itself will look as follows:

````
{
  "iss": "Apache Ofbiz",
  "aud": "To whom it may concern",
  "exp": 1588977215,
  "jti": "ZnXt4dVTEqMIPLP4Ly7U8A",
  "iat": 1588976615,
  "nbf": 1588976495,
  "secret": "eyJhbGciOiJBMTI4S1ciLCJlbmMiOiJBMTI4Q0JDLUhTMjU2In0.W2HuUyz2hUrZqMpAqUoWqhumWxzEcpjueUY98z9xe9atBGgfYYOr2A.BzrhVTN1XyKpChir-gkVNA.95HOjNiVdjzLyfgSxx7sINhhCJqFTxid9PEMCnI7-V9qwIBuNJYNvUS7lNQKTKdNUSrXQsz_NYms7c6QYtXeDw.bSkKfmUJIHtAlcp8n3Ay9Q"
}
````