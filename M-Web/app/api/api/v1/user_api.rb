module Api::V1
  class UserApi < Grape::API
    namespace :users do
      params do
        requires :email, type: String
        requires :password, type: String
        requires :android_id, type: String
        requires :android_name, type: String
      end

      post :signin do
        user = User.find_by(email: params[:email])
        if user.valid_password?(params[:password])
          token = loop do
            random_token = SecureRandom.urlsafe_base64(nil, true)
            break random_token unless Token.exists?(value: random_token)
          end
          user.tokens.create(value: token, android_id: params[:android_id],android_name: params[:android_name] )
          {
            time: Time.now.to_s,
            status: 'success',
            token: token,
            email: user.email,
            first_name: user.first_name,
            last_name: user.last_name,
            avatar_url: 'https://i.imgur.com/zQlV8eE.jpg',
            verified: "true",
            used: "0.2",
            is_vip: '0'
          }
        else
          {
            time: Time.now.to_s,
            status: 'error',
            message: 'Invalid email or password!'
          }
        end
      end

      params do
        requires :email, type: String
        requires :password, type: String
        requires :first_name, type: String
        requires :last_name, type: String
      end
      post :signup do
        if User.create!(declared(params))
          {
            time: Time.now.to_s,
            status: 'success',
            message: 'Registration successful. You can log in now!'
          }
        end
      end




      params do
        requires :email, type: String
      end
      post :forget_password do
        if User.find_by_email(params[:email])
          {
            time: Time.now.to_s,
            status: 'success',
            message: 'Reset Password email has been sent!'
          }
        else
          {
            time: Time.now.to_s,
            status: 'error',
            message: "User doesn't exist!"
          }
        end
      end


      post :logout do
        if current_token
          Token.destroy(current_token.id)
          {
            time: Time.now.to_s,
            status: 'success',
            message: 'You have successfully logged out!'
          }
        else
          {
            time: Time.now.to_s,
            status: 'error',
            message: 'You need to be logged in to log out!'
          }
        end
      end
    end
  end
end
