module Api::V2
  class FileApi < Grape::API
    namespace :files do


      get 'jpg' do
        {
          time: Time.now.to_s,
          status: 'success',
          message: nil,
          data: {
            id: "dhdth",
            name: "laptopmockup_sliderdy.jpg",
            url: 'https://feseuel.com/wp-content/uploads/revslider/homeslider_boxed/laptopmockup_sliderdy.jpg',
            size: "852",
            type: "jpg"
          }
        }
      end

      get 'png' do
        {
          time: Time.now.to_s,
          status: 'success',
          message: nil,
          data: {
            id: "segvrseg",
            name: "macbookpro.png",
            url: 'https://feseuel.com/wp-content/uploads/revslider/web-product-dark/macbookpro.png',
            size: "852",
            type: "png"
          }
        }
      end

      get 'mp3' do
        {
          time: Time.now.to_s,
          status: 'success',
          message: nil,
          data: {
            id: "mp3",
            name: "Mp3",
            url: 'https://feseuel.com/wp-content/uploads/2018/07/y2mate.com-giot_nang_ben_them_cover_edward_duong_nguyen_ze_ri7LEqbnpio.mp3',
            size: "852",
            type: "mp3"
          }
        }
      end

      get 'mp4' do
        {
          time: Time.now.to_s,
          status: 'success',
          message: nil,
          data: {
            id: "segvrseg",
            name: "video",
            url: 'https://feseuel.com/wp-content/uploads/2018/07/video.mp4',
            size: "852",
            type: "mp4"
          }
        }
      end

      # params do
      #   requires :file_id, type: String
      # end
      # get ':file_id' do
      #   {
      #     time: Time.now.to_s,
      #     status: 'success',
      #     message: nil,
      #     user:{
      #           email: current_user.email,
      #           first_name: current_user.first_name,
      #           last_name: current_user.last_name,
      #           avatar_url: 'https://i.imgur.com/zQlV8eE.jpg',
      #           verified: "true",
      #           used: "0.2",
      #           is_vip: '0'
      #     },
      #     files: {
      #           id: "segvrseg",
      #           name: "AAAAAAAAAAAA",
      #           size: "852",
      #           type: "jpg",
      #           url: 'https://i.imgur.com/zQlV8eE.jpg'
      #     }
      #   }
      # end
      #
      # post do
      #   {
      #     time: Time.now.to_s,
      #     status: 'success',
      #     message: nil,
      #     user:{
      #           email: current_user.email,
      #           first_name: current_user.first_name,
      #           last_name: current_user.last_name,
      #           avatar_url: 'https://i.imgur.com/zQlV8eE.jpg',
      #           verified: "true",
      #           used: "0.2",
      #           is_vip: '0'
      #     },
      #     clouds: Cloud.all
      #   }
      # end
      #
      # params do
      #   requires :file_id, type: String
      # end
      # put ':id' do
      #   {
      #     time: Time.now.to_s,
      #     status: 'success',
      #     message: nil,
      #     user:{
      #           email: current_user.email,
      #           first_name: current_user.first_name,
      #           last_name: current_user.last_name,
      #           avatar_url: 'https://i.imgur.com/zQlV8eE.jpg',
      #           verified: "true",
      #           used: "0.2",
      #           is_vip: '0'
      #     },
      #     clouds: Cloud.all
      #   }
      # end
      #
      # params do
      #   requires :file_id, type: String
      # end
      # delete ':id' do
      #   {
      #     time: Time.now.to_s,
      #     status: 'success',
      #     message: nil,
      #     user:{
      #           email: current_user.email,
      #           first_name: current_user.first_name,
      #           last_name: current_user.last_name,
      #           avatar_url: 'https://i.imgur.com/zQlV8eE.jpg',
      #           verified: "true",
      #           used: "0.2",
      #           is_vip: '0'
      #     },
      #     clouds: Cloud.all
      #   }
      # end
    end
  end
end
